package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

@Route
public class EditorInDialog extends Div {

    public EditorInDialog() {
        Dialog dialog = new Dialog();
        Dialog dialog2 = new Dialog();

        TinyMce editor = new TinyMce();
        editor.configure("plugins", "link");

        TinyMce editor2 = new TinyMce();
        editor2.configure("plugins", "link");

        TinyMce editor3 = new TinyMce();
        editor3.configure("plugins", "link");

        TinyMce editor4 = new TinyMce();
        editor4.configure("plugins", "link");

        editor.addValueChangeListener(e -> {
            reportValue(e.getValue());
        });

        dialog.add(new Div(new HorizontalLayout(editor, editor2), new Button("show value", e -> {
            reportValue(editor.getValue());
        }), new Button("close", e -> {
            dialog.close();
        })));

        dialog2.add(new Div(new HorizontalLayout(editor4), new Button("show value", e -> {
            reportValue(editor.getValue());
        }), new Button("close", e -> {
            dialog.close();
        })));

        Button button = new Button("Open the editor dialog");
        button.addClickListener(event -> {
            dialog.open();
        });

        Button button2 = new Button("Open the editor dialog");
        button2.addClickListener(event -> {
            dialog2.open();
        });

        add(button, button2, editor3);

    }

    private void reportValue(String value) {
        // Not using notifications here because of regression in V24:
        // https://github.com/vaadin/flow-components/issues/4896
        add(new Paragraph(("Value now:" + value)));
    }

}
