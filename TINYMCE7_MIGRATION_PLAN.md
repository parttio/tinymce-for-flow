# Plan: Migrate TinyMCE for Flow from v6 to v7

## Context

The project bundles TinyMCE **6.6.1** (vendored under `src/main/resources/META-INF/resources/frontend/tinymce_addon/tinymce/`). TinyMCE 7 (released 2024) introduced:
- Removal of `tabfocus`, `template` plugins from self-hosted open-source
- `advlist` merged into the `lists` plugin (passing both is harmless but `advlist` alone adds nothing)
- `accordion` plugin became open-source (was premium in v6)
- Toolbar button `fontname` renamed to `fontfamily`
- New `fontsizeinput` toolbar button
- `sandbox_iframes` now defaults to `true` (behaviour change for embedded iframe content)
- The JS init/event/content API is fully backwards compatible

**Goal:** Replace the bundled JS with TinyMCE 7, update Java enums to reflect v7 API changes, fix the internal default toolbar/plugin config that would silently break — all with no Java compile-time breaking changes (deprecated constants are kept, new ones added).

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/resources/META-INF/resources/frontend/tinymce_addon/tinymce/` | Full directory replacement with TinyMCE 7 bundle |
| `src/main/java/org/vaadin/tinymce/Plugin.java` | Add `ACCORDION`, add `@Deprecated` to removed plugins |
| `src/main/java/org/vaadin/tinymce/Toolbar.java` | Add `FONT_FAMILY` + `FONT_SIZE_INPUT`, add `@Deprecated` to renamed/removed buttons |
| `src/main/java/org/vaadin/tinymce/TinyMce.java` | Fix `createBasicTinyMce()`: replace `FORMAT_SELECT` → `BLOCKS`, remove `ADVLIST` |
| `src/main/resources/META-INF/resources/frontend/tinymceLumo.css` | Review `.tox-*` selectors for TinyMCE 7 skin compatibility |
| `src/main/resources/META-INF/resources/frontend/tinymceConnector.js` | No changes needed (TinyMCE 7 JS API is fully compatible; `tox-tinymce-aux` confirmed present in v7) |

---

## Step-by-Step Implementation

### Step 1 — Download TinyMCE 7

```bash
cd /tmp && npm install tinymce@7 --no-save
# installs latest stable TinyMCE 7.x into /tmp/node_modules/tinymce/
```

Pin the exact patch version used (e.g., `7.6.0`) in a comment in `TinyMce.java`'s `injectTinyMceScript()` for traceability.

### Step 2 — Replace Bundled TinyMCE

> **IMPORTANT — token efficiency:** Use only Bash commands for Steps 1 & 2. Do NOT use the Read tool on any TinyMCE files (tinymce.min.js is ~6MB, plugins are large). Just run the shell commands below — no file inspection needed. If you encounter issues during this process, ask the user before using other tooling.

```bash
rm -rf src/main/resources/META-INF/resources/frontend/tinymce_addon/tinymce/
cp -r /tmp/node_modules/tinymce/ src/main/resources/META-INF/resources/frontend/tinymce_addon/tinymce/
```

> TinyMCE 7 npm package keeps the same directory layout. The `context://frontend/tinymce_addon/tinymce/tinymce.min.js` path used in `TinyMce.java:injectTinyMceScript()` remains valid.

### Step 3 — Update `Plugin.java`

**Add** (new in TinyMCE 7 open-source):
```java
ACCORDION("accordion"),
```

**Add `@Deprecated`** to plugins absent from TinyMCE 7 self-hosted:

| Enum constant | Reason |
|---------------|--------|
| `COLORPICKER` | Removed in TinyMCE 5 |
| `CONTEXTMENU` | Removed in TinyMCE 5 |
| `FULLPAGE` | Removed in TinyMCE 6 |
| `HR` | Removed in TinyMCE 6 (now native element) |
| `IMAGE_TOOLS` | Removed in TinyMCE 6 |
| `LEGACYOUTPUT` | Removed in TinyMCE 6 |
| `PASTE` | Merged into core in TinyMCE 6 |
| `PRINT` | Removed in TinyMCE 6 |
| `SPELLCHECKER` | Cloud-only since TinyMCE 6, not self-hosted |
| `TABFOCUS` | **Removed in TinyMCE 7** |
| `TEMPLATE` | **Removed in TinyMCE 7** (use premium `advtemplate`) |
| `TEXT_COLOR` | Removed in TinyMCE 6 |
| `ADVLIST` | **Merged into `lists` in TinyMCE 7** — use `Plugin.LISTS` |

### Step 4 — Update `Toolbar.java`

**Add** (new / correct names in TinyMCE 7):
```java
FONT_FAMILY("fontfamily"),       // replaces FONTNAME, renamed in TinyMCE 7
FONT_SIZE_INPUT("fontsizeinput"), // new direct-input font size widget in TinyMCE 7
```

**Add `@Deprecated`** to renamed/removed toolbar buttons:

| Enum constant | Reason |
|---------------|--------|
| `FONTNAME` | Renamed to `fontfamily` in TinyMCE 7; use `FONT_FAMILY` |
| `FORMAT_SELECT` | Renamed to `blocks` in TinyMCE 6; `BLOCKS` already exists |
| `FONTSELECT` | String value `"FONTSELECT"` is wrong case — was never functional; use `FONT_FAMILY` |
| `FONTSIZESELECT` | String value `"FONTSIZESELECT"` is wrong case — was never functional; use `FONTSIZE` |
| `PRINT` | Plugin was removed in TinyMCE 6 |

### Step 5 — Fix `createBasicTinyMce()` in `TinyMce.java` (line 380)

This internal default configuration used deprecated/broken constants. Update to valid TinyMCE 7 equivalents (not a public API change):

- `Toolbar.FORMAT_SELECT` → `Toolbar.BLOCKS`
- `Plugin.ADVLIST` removed from the plugin list

Before:
```java
this.configurePlugin(false, Plugin.ADVLIST, Plugin.AUTOLINK, Plugin.LISTS, Plugin.SEARCH_REPLACE);
this.configureToolbar(false, ..., Toolbar.FORMAT_SELECT, ...);
```

After:
```java
this.configurePlugin(false, Plugin.AUTOLINK, Plugin.LISTS, Plugin.SEARCH_REPLACE);
this.configureToolbar(false, ..., Toolbar.BLOCKS, ...);
```

### Step 6 — Review `tinymceLumo.css`

`src/main/resources/META-INF/resources/frontend/tinymceLumo.css` uses TinyMCE internal class names. Most are stable v6→v7, but verify:

- **`.tox-toolbar-overlord`** (lines 7, 19, 25) — TinyMCE 7 reorganised the toolbar DOM. This class may no longer exist; update selectors if needed.
- `.tox-toolbar__primary`, `.tox-editor-header`, `.tox-menubar`, `.tox-tbtn`, `.tox-mbtn` — expected to still be present.

If `.tox-toolbar-overlord` is gone, toolbar background/padding styling silently stops applying (visual regression, not functional).

### Step 7 — Note `sandbox_iframes` Behaviour Change

Add a Javadoc comment to `TinyMce.java` (class-level or in `setConfig()`) noting:

> **TinyMCE 7 change:** `sandbox_iframes` defaults to `true` in TinyMCE 7, which sandboxes any iframes in editor content. To restore the TinyMCE 6 behaviour, add `configure("sandbox_iframes", false)`.

---

## What Does NOT Change

- `tinymceConnector.js` — all TinyMCE 7 JS APIs are backwards compatible
- `Language.java` enum — language codes are the same; lang files are replaced as part of the bundle swap in Step 2
- `Menubar.java` — no menubar changes in TinyMCE 7
- `ValueChangeMode.java` — no changes
- Java class structure, method signatures, package names — all unchanged

---

## Verification

```bash
# 1. Full build
mvn clean package

# 2. Start demo server
mvn spring-boot:run -pl . -Dspring-boot.run.main-class=org.vaadin.tinymce.Application

# 3. Manual checks in browser:
#    a. http://localhost:8080/ — editor renders with TinyMCE 7 UI
#       - In browser console: tinymce.majorVersion should be "7"
#       - Typing, value-change events, blur/focus all work
#       - No console errors or warnings about unknown plugins
#    b. http://localhost:8080/editor-in-dialog — editor inside Dialog
#       - Menus/dropdowns open correctly (tox-tinymce-aux workaround)
#       - Keyboard navigation works inside dialog
#    c. http://localhost:8080/image-uploads-enabled-view — image upload flow
#    d. Browser console is clean (no TinyMCE warnings about removed plugins)

# 4. Run E2E smoke test
mvn test -Dtest=MopoSmokeTest
```
