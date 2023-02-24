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

## Cutting a release

Before cutting a release, make sure the build passes properly locally and in GitHub Actions based verification build.

To tag a release and increment versions, issue:

    mvn release:prepare release:clean

Answer questions, defaults most often fine.
Note that `release:perform` is not needed as there is a GitHub Action is set up build and to push release to Maven Central automatically. 

Then push the tag (and new development version) to SCM (this triggers GitHub Action to execute):

    git push

Directory will automatically pick up new releases within about half an hour, but if browser or Vaadin version support change, be sure to adjust the metadata in Vaadin Directory UI.
