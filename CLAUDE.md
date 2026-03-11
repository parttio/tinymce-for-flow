# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TinyMCE for Flow — a Vaadin 25 add-on that wraps the TinyMCE rich text editor as a Java server-side component. The component value is plain HTML and it implements `HasValue` for Vaadin Binder compatibility.

**Maven coordinates:** `org.parttio:tinymce-for-flow`
**Java package:** `org.vaadin.tinymce`
**Java version:** 21
**Stack:** Vaadin 25, Spring Boot 4

## Build & Test Commands

```bash
mvn clean package            # Full build (compile + test + package)
mvn compile                  # Compile only
mvn test                     # Run all tests (requires Playwright/Chromium)
mvn test -Dtest=MopoSmokeTest            # Run a single test class
mvn test -Dtest=MopoSmokeTest#smokeTest  # Run a single test method
```

The test suite uses Spring Boot with `RANDOM_PORT` and Playwright (via [Mopo](https://github.com/nickvdyck/mopo)) for browser-based E2E tests. Tests launch a real Chromium browser.

## Running the Demo Server

```bash
mvn spring-boot:run -pl . -Dspring-boot.run.main-class=org.vaadin.tinymce.Application
```

Test/demo views are `@Route`-annotated classes in `src/test/java` (e.g., `DemoView`, `EditorInDialog`, `ImageUploadsEnabledView`).

## Architecture

### Server-side (Java)

- **`TinyMce`** (`src/main/java/.../TinyMce.java`) — The main component. Extends `AbstractCompositeField<Div, TinyMce, String>` so it works with Vaadin's data binding. Manages editor lifecycle (attach/detach/reattach), config assembly, and JS connector initialization.
- **`Plugin`**, **`Toolbar`**, **`Menubar`** — Enums providing type-safe constants for TinyMCE plugin names, toolbar buttons, and menu sections.
- **`Language`** — Enum for supported TinyMCE UI languages.
- **`ValueChangeMode`** — Enum controlling when value changes propagate (BLUR, TIMEOUT, CHANGE).

Configuration is accumulated into a `tools.jackson.databind.node.ObjectNode config` field via `configure()` / `configurePlugin()` / `configureToolbar()` / `configureMenubar()` methods and serialized inline into the JS connector call on attach. A raw JS config string can also be set via `setConfig(String)`.

### Jackson 3

Vaadin 25 ships with **Jackson 3**, which uses `tools.jackson.*` packages — not the older `com.fasterxml.jackson.*`. Use `tools.jackson.databind.node.ObjectNode`, `ArrayNode`, `JsonNodeFactory`, `JsonNode` for any JSON handling. Vaadin's own `DomEvent.getEventData()` also returns a `tools.jackson.databind.JsonNode` in Vaadin 25.

### Client-side (JavaScript)

- **`tinymceConnector.js`** (`src/main/resources/META-INF/resources/frontend/`) — The connector that bridges Vaadin's server-side component to the TinyMCE JS library. Handles `initLazy`, content sync via custom DOM events (`tchange`, `tblur`, `tfocus`), and Dialog/shadow-DOM workarounds.
- **Bundled TinyMCE** — A full TinyMCE distribution lives under `src/main/resources/META-INF/resources/frontend/tinymce_addon/tinymce/`. This is vendored; do not edit these files.

### Key design notes

- The component does **not** use Shadow DOM by default. The deprecated `TinyMce(boolean shadowRoot)` constructor exists for legacy Dialog support but is no longer needed.
- On detach, `tinymce.get(id).remove()` is called to clean up the TinyMCE instance. On reattach, the connector re-initializes.
- TinyMCE doesn't work well inside Shadow DOM — menu overlays and keyboard navigation require special handling (see the `isInDialog` workaround in the connector).
- The `vaadin-dev` dependency (optional, with `copilot` excluded) is required in the main dependencies for the Vaadin dev-mode server to start during tests. Without it, `'vaadin-dev-server' not found` is thrown at servlet context initialization.

## Release Process

```bash
mvn release:prepare release:clean
```

A GitHub Action handles `release:perform` — it builds and pushes to Maven Central automatically on tagged commits.
