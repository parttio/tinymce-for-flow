package org.vaadin.tinymce;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.tinymce.TinyMce;

@Route
public class EditorInDialog extends Div {
    
    public EditorInDialog() {
        Dialog dialog = new Dialog();
        dialog.setModal(false);
        
        TinyMce editor = new TinyMce(true);
        editor.configure("plugins", "link");

        editor.addValueChangeListener(e -> {
            reportValue(e.getValue());
        });
        
        dialog.add(new Div(editor, new Button("show value", e ->{reportValue(editor.getValue());})));
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
