# TinyMCE for Flow

A Vaadin 25 Java integration for TinyMCE, a popular open-source rich text editor. The component implements `HasValue` and works with Vaadin's data binding. Content value is plain HTML.

## Usage

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.parttio</groupId>
    <artifactId>tinymce-for-flow</artifactId>
    <version>VERSION</version>
</dependency>
```

Create and use the editor:

```java
TinyMce editor = new TinyMce();
editor.setValue("<p>Hello, world</p>");

// Or use a preconfigured setup
TinyMce basicEditor = new TinyMce()
    .configureToolbar(true, Toolbar.BOLD, Toolbar.ITALIC, Toolbar.UNDERLINE)
    .configurePlugin(true, Plugin.LISTS);
```

## Limitations

TinyMCE 7 defaults to sandboxing any iframes in editor content with `sandbox_iframes: true`. If your content includes iframes and they appear broken, disable sandboxing:

```java
editor.configure("sandbox_iframes", false);
```

If you cannot trust your users' HTML input, apply a converter that filters it (e.g., using the JSOUP library) before storing or displaying it.

## Upgrade Guide

When upgrading to version 5.x (Vaadin 25 / TinyMCE 7):

- **Java 21, Vaadin 25, Spring Boot 4** are now required
- **Plugin.ADVLIST** is no longer needed — the `lists` plugin handles it automatically
- **Plugin.TEMPLATE** and **Plugin.TABFOCUS** are removed in TinyMCE 7
- **Toolbar.FONTNAME** has been renamed to **Toolbar.FONT_FAMILY**; new **Toolbar.FONT_SIZE_INPUT** is available
- **sandbox_iframes** defaults to `true` — note if iframe content looks broken
- Dialog usage: no special configuration needed anymore

## Development

### Starting the demo server

```bash
mvn spring-boot:run -pl . -Dspring-boot.run.main-class=org.vaadin.tinymce.Application
```

Demo views are `@Route`-annotated classes in `src/test/java`. Access them at http://localhost:8080.

### Running tests

```bash
mvn test                                   # Run all tests
mvn test -Dtest=MopoSmokeTest              # Run a specific test class
```

Tests use Spring Boot with Playwright (via Mopo) for browser-based E2E testing.

## Release Process

To tag a release and increment versions:

```bash
mvn release:prepare release:clean
```

Answer the prompts (defaults are usually fine). A GitHub Action automatically builds and deploys the release to Maven Central.
