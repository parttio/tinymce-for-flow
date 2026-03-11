# TinyMCE for Flow — Dual Version Support Plan (v6 + v7)

**Goal:** Ship both TinyMCE 6.6.1 (free/self-hosted) and TinyMCE 7.9.2 (paid/cloud-capable) in a single JAR. Developers choose the version by instantiating `TinyMce6` (v6) or `TinyMce` (v7, default, backwards-compatible).

---

## 1. Class Hierarchy Redesign

The existing `TinyMce` class stays as the concrete TinyMCE 7 component to preserve backwards compatibility for all current users. A new `TinyMce6` subclass is added for TinyMCE 6 users.

```
AbstractCompositeField<Div, TinyMce, String>  (Vaadin framework)
    └── TinyMce          (concrete, TinyMCE 7.9.2 — current class, unchanged public API)
            └── TinyMce6 (concrete, TinyMCE 6.6.1 — new subclass)
```

**Rationale for this hierarchy over a shared abstract base:**

- Backwards compatibility is the primary constraint. Every existing user of `TinyMce` must continue to get TinyMCE 7 without any change.
- `TinyMce` already has the correct Vaadin `@JavaScript` / `@StyleSheet` annotations and `injectTinyMceScript()` hook. The hook was specifically designed to be overridable.
- Making `TinyMce` abstract would be a breaking API change (existing code doing `new TinyMce()` would fail to compile).
- All meaningful logic — attach/detach lifecycle, config assembly, connector initialisation, `HasValue` plumbing — is identical between versions and lives in `TinyMce`. `TinyMce6` only overrides the two version-specific points.

**Alternative considered and rejected:** Introducing a new abstract `AbstractTinyMce` superclass and making `TinyMce` → `TinyMce7`. This would break all existing source that references `TinyMce`. If a rename to `TinyMce7` is desired in a later major release, the path is open: `TinyMce7` could simply be added as a subclass alias now and `TinyMce` deprecated-for-removal in a future version.

---

## 2. Bundle Directory Layout

The current TinyMCE 7 bundle stays exactly where it is. The TinyMCE 6 bundle is placed in a parallel sibling directory:

```
src/main/resources/META-INF/resources/frontend/
├── tinymceConnector.js          (shared, unchanged)
├── tinymceLumo.css              (shared, unchanged)
└── tinymce_addon/
    ├── tinymce/                 ← TinyMCE 7.9.2 (current, untouched)
    │   ├── tinymce.min.js
    │   ├── plugins/...
    │   └── ...
    └── tinymce6/                ← TinyMCE 6.6.1 (new, restored from git)
        ├── tinymce.min.js
        ├── langs/...
        ├── plugins/...
        └── ...
```

The two bundles are completely independent on disk. Vaadin's static resource serving maps the entire `frontend/` tree; both directories are served without any configuration.

**Key difference:** TinyMCE 6 had a `langs/` directory (20 language files) and a `template` plugin that were removed from TinyMCE 7. Each directory simply contains what belongs to that version.

---

## 3. Version-Specific Script Injection

`TinyMce.java` already has a clean hook for this:

```java
// In TinyMce (v7) — current implementation, unchanged:
protected void injectTinyMceScript() {
    getUI().get().getPage().addJavaScript(
            "context://frontend/tinymce_addon/tinymce/tinymce.min.js");
}
```

`TinyMce6` overrides only this one method:

```java
// In TinyMce6 (v6):
@Override
protected void injectTinyMceScript() {
    getUI().get().getPage().addJavaScript(
            "context://frontend/tinymce_addon/tinymce6/tinymce.min.js");
}
```

The `@JavaScript` annotation on `TinyMce` loads `tinymceConnector.js` at class-load time. Because `TinyMce6` extends `TinyMce`, the annotation is inherited — the connector loads correctly for both. **No changes to the connector loading path are needed.**

---

## 4. Enum Strategy

**Keep a single set of shared enums with improved Javadoc. Do not split into version-specific enums.**

Rationale: split enums would mean users cannot write version-agnostic helper methods accepting `Plugin...`, defeating usability. The existing `@Deprecated` annotations are too blunt for items that are valid in v6 but removed in v7.

**Specific enum changes required:**

`Plugin.java` — change `@Deprecated` to Javadoc-only note for items valid in v6 but removed in v7:
- `ADVLIST` → remove `@Deprecated`, add: *"Valid for TinyMCE 6. In TinyMCE 7 its functionality is merged into {@link #LISTS}."*
- `TABFOCUS` → remove `@Deprecated`, add: *"Removed in TinyMCE 7. Only usable with {@link TinyMce6}."*
- `TEMPLATE` → remove `@Deprecated`, add: *"Removed in TinyMCE 7. Only usable with {@link TinyMce6}. For TinyMCE 7 use the premium {@code advtemplate} plugin."*
- Items removed in v5/v6 (`COLORPICKER`, `CONTEXTMENU`, `FULLPAGE`, `HR`, `IMAGE_TOOLS`, `LEGACYOUTPUT`, `PASTE`, `PRINT`, `SPELLCHECKER`, `TEXT_COLOR`) stay `@Deprecated` — unusable in either supported version.

`Toolbar.java`:
- `FONTNAME` → remove `@Deprecated`, add: *"TinyMCE 6 name for the font family selector. Use {@link #FONT_FAMILY} for TinyMCE 7."*
- `FONT_FAMILY`, `FONT_SIZE_INPUT` → add Javadoc: *"TinyMCE 7 only. Not available in TinyMCE 6."*

---

## 5. Restoring the v6 Bundle

The complete TinyMCE 6.6.1 bundle is preserved in git at commit `6027b41` (last commit before the migration). Extract into `tinymce6/` without touching the v7 bundle:

```bash
PREFIX="src/main/resources/META-INF/resources/frontend/tinymce_addon/tinymce"
git ls-tree -r 6027b41 "$PREFIX/" --name-only | while read f; do
    rel="${f#$PREFIX/}"
    dest="src/main/resources/META-INF/resources/frontend/tinymce_addon/tinymce6/$rel"
    mkdir -p "$(dirname "$dest")"
    git show "6027b41:$f" > "$dest"
done
```

**Do not** use `git checkout 6027b41 -- <path>` — that would overwrite the v7 bundle in the working tree.

---

## 6. createBasicTinyMce() and Version Differences

`createBasicTinyMce()` is currently `private` in `TinyMce` — must be widened to `protected` to allow subclass override.

Similarly, `basicTinyMCECreated` (boolean field) must be widened to `protected`, or a `protected void markBasicTinyMceCreated()` setter added.

**`TinyMce6` override** restores the v6 defaults:

```java
@Override
protected TinyMce createBasicTinyMce() {
    setValue("");
    this.configure("branding", false);
    this.basicTinyMCECreated = true;
    this.configurePlugin(false, Plugin.ADVLIST, Plugin.AUTOLINK,
            Plugin.LISTS, Plugin.SEARCH_REPLACE);
    this.configureMenubar(false, Menubar.FILE, Menubar.EDIT, Menubar.VIEW,
            Menubar.FORMAT);
    this.configureToolbar(false, Toolbar.UNDO, Toolbar.REDO,
            Toolbar.SEPARATOR, Toolbar.FORMAT_SELECT, Toolbar.SEPARATOR,
            Toolbar.BOLD, Toolbar.ITALIC, Toolbar.SEPARATOR,
            Toolbar.ALIGN_LEFT, Toolbar.ALIGN_CENTER, Toolbar.ALIGN_RIGHT,
            Toolbar.ALIGN_JUSTIFY, Toolbar.SEPARATOR, Toolbar.OUTDENT,
            Toolbar.INDENT);
    return this;
}
```

No other method overrides needed in `TinyMce6`.

---

## 7. Connector JS

No changes needed. `tinymceConnector.js` is version-agnostic. All TinyMCE APIs used (`tinymce.init`, `tinymce.get(id).remove()`, `editor.getContent()`, `editor.setContent()`, `editor.selection.setContent()`, `editor.mode.set()`, `editor.focus()`, event names) are identical in v6 and v7. The dialog hack (`tox-tinymce-aux`) also exists in both versions.

---

## 8. tinymceLumo.css

No changes needed. The `.tox-*` CSS class names are stable across v6 and v7. The single shared `tinymceLumo.css` works for both.

If future testing reveals visual regressions specific to one version, add a version class to the wrapper element from Java (e.g. `getElement().getClassList().add("tinymce-v6")`) and write scoped CSS rules. Not needed now.

---

## 9. Test/Demo Views

Existing test views all instantiate `new TinyMce()` and continue to exercise TinyMCE 7 — no changes needed to keep the existing suite passing.

Add new demo views in `src/test/java/org/vaadin/tinymce/`:

- **`TinyMce6DemoView`** (route: `tinymce6`) — counterpart of `DemoView` using `new TinyMce6()`
- **`TinyMce6WithAdditionalConfig`** — counterpart of `TinyMceWithAdditionalConfig` using `TinyMce6` + `configurePlugin(true, Plugin.TABLE)`
- **`EditorInDialogBothVersions`** — opens dialogs with both `TinyMce6` and `TinyMce` to confirm `tox-tinymce-aux` workaround works for both

`MopoSmokeTest.smokeTest()` iterates all routes automatically — new `@Route` views are covered without test class changes. Add a `menuConfigV6()` test method analogous to `menuConfig()` for v6-specific basic configuration verification.

---

## 10. Backwards Compatibility

Existing users of `TinyMce` are fully unaffected:

- `TinyMce` remains concrete, instantiable, non-abstract.
- Its public API surface is unchanged.
- It still injects TinyMCE 7 from `tinymce_addon/tinymce/tinymce.min.js`.
- The only source-level change visible: `createBasicTinyMce()` widens from `private` to `protected`. This is a binary-compatible widening — not a breaking change.
- Enum items changing from `@Deprecated` to Javadoc-only comments will *stop* emitting compile warnings for v6 users — a net improvement.

---

## Implementation Sequence

| Step | Description | Commit message |
|------|-------------|---------------|
| 1 | Restore v6 bundle into `tinymce6/` via git shell loop | `Add TinyMCE 6.6.1 bundle under tinymce6/` |
| 2 | Widen `createBasicTinyMce()` and `basicTinyMCECreated` to `protected` in `TinyMce.java` | `Widen createBasicTinyMce visibility for subclass override` |
| 3 | Create `TinyMce6.java` with `injectTinyMceScript()` and `createBasicTinyMce()` overrides | `Add TinyMce6 component for TinyMCE 6.6.1 self-hosted use` |
| 4 | Update enum Javadoc for dual-version accuracy | `Improve Plugin/Toolbar enum Javadoc for dual-version clarity` |
| 5 | Add v6 demo/test views and `menuConfigV6()` in `MopoSmokeTest` | `Add TinyMce6 demo views and smoke test coverage` |
| 6 | Update `TinyMce.java` class-level Javadoc | `Update TinyMce Javadoc to document dual-version support` |

---

## Verification

```bash
mvn clean package
mvn test -Dtest=MopoSmokeTest
# manual checks:
# http://localhost:PORT/tinymce6           → TinyMCE 6 renders, console: tinymce.majorVersion === "6"
# http://localhost:PORT/demo               → TinyMCE 7 unchanged
# http://localhost:PORT/editorindialog     → dialog menus work (v7)
# http://localhost:PORT/editor-in-dialog-both-versions → dialog menus work for both
```

---

## File Size Impact

The v6 bundle adds ~3–4 MB to the JAR (same order as the v7 bundle). At runtime, only the bundle for the instantiated class is loaded — a v7-only application never fetches `tinymce6/tinymce.min.js`.
