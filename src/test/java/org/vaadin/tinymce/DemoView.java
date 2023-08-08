package org.vaadin.tinymce;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.tinymce.TinyMce;

@Route
public class DemoView extends Div {
    

    protected TinyMce tinyMce;

    public DemoView() {
        tinyMce = new TinyMce();

        tinyMce.setEditorContent("<p>Voi <strong>jorma</strong>!<p>");
        tinyMce.setHeight("700px");
        
        add(tinyMce);

        Button b = new Button("Set content dynamically", e -> {
            tinyMce.setEditorContent("New value");
        });
        add(b);

        Button b2 = new Button("Show content", e -> {
            Notification.show(tinyMce.getCurrentValue());
        });
        add(b2);
        
        Button focus = new Button("focus", e->{
            tinyMce.focus();
        });
        add(focus);
        tinyMce.addFocusListener(e->{
            Notification.show("Focus event!");
        });
        tinyMce.addBlurListener(e->{
            Notification.show("Blur event!");
        });

        Button blur = new Button("blur (NOT SUPPORTED)", e-> {
            tinyMce.blur();
        });
        blur.addClickShortcut(Key.KEY_B, KeyModifier.CONTROL);
        add(blur);

        Button disable = new Button("Disabble", e-> {
            tinyMce.setEnabled(!tinyMce.isEnabled());
            e.getSource().setText(tinyMce.isEnabled() ? "Disable" : "Enable");
        });
        add(disable);
        
        tinyMce.addValueChangeListener(e -> {
            Notification.show("ValueChange event!");
            System.out.println(e.getValue());
        });

    }

}
