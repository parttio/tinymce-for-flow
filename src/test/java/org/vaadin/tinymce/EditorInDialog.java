package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;

@Route
public class EditorInDialog extends Div {

    public EditorInDialog() {
        Dialog dialog = new Dialog();
//        dialog.setModal(false);

        TinyMce editor = new TinyMce();
        editor.configure("plugins", "link");

        editor.addValueChangeListener(e -> {
            reportValue(e.getValue());
        });

        dialog.add(new Div(editor, new Button("show value", e -> {
            reportValue(editor.getValue());
        }), new Button("close", e -> {
            dialog.close();
        })));
        Button button = new Button("Open the editor dialog");
        button.addClickListener(event -> {
            dialog.open();
        });

        add(button);

    }

    private void reportValue(String value) {
        // Not using notifications here because of regression in V24:
        // https://github.com/vaadin/flow-components/issues/4896
        add(new Paragraph(("Value now:" + value)));
    }

}
