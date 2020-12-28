package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.tinymce.TinyMce;

@Route
public class EditorInDialog extends Div {
    
    public EditorInDialog() {
        Dialog dialog = new Dialog();
        
        TinyMce editor = new TinyMce(true);
        editor.configure("plugins", "link");
        
        editor.addValueChangeListener(e -> 
            Notification.show("Value now:" + e.getValue())
        );
        
        dialog.add(new Div(editor, new Button("show value", e ->{Notification.show(editor.getValue());})));
        Button button = new Button("Open the editor dialog");
        button.addClickListener(event -> {
            dialog.open();
        });
        
        add(button);
    }
    
}
