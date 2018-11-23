# TinyMCE for Flow

Vaadin 10 Java integration for TinyMCE text editor. 

Works with binder as the component implements HasValue interfaces. The value is plain HTML. If you can't trust your clients, apply converter that filters the input with e.g. JSOUP library.

Builds will be available from https://vaadin.com/directory 

## Limitations

TinyMCE (like most traditional wysiwyg editors) don't work inside shadow DOM. You most probably have issues if you use templates or use the editor in Dialog.

## Development instructions

Starting the test/demo server:
```
mvn jetty:run
```

This deploys demo at http://localhost:8080

