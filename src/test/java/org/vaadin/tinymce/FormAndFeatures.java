package org.vaadin.tinymce;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.RichText;

@Route
public class FormAndFeatures extends FormLayout {


    protected TinyMce tinyMce;

    public FormAndFeatures() {

        tinyMce = new TinyMce();
        tinyMce.setLabel("Label for RTE");
        // TODO tinyMce.setRequired(true);
        tinyMce.setRequiredIndicatorVisible(true);
        tinyMce.setValue("<p>Voi <strong>jorma</strong>!<p>");
        tinyMce.setHeight("400px");
        
        add(tinyMce);

        TextField textField = new TextField("Normal text field");
        textField.setValue("This is here for a reference...");
        textField.setRequired(true);
        add(textField);

        Button b = new Button("Set content dynamically", e -> {
            tinyMce.setValue("New value");
        });
        add(b);

        Button b2 = new Button("Show content", e -> {

            var n = new Notification("", 3000);
            n.add(new VerticalLayout(
                    new H5("New value:"),
                    new RichText(tinyMce.getCurrentValue())
                    )
            );
            n.open();
        });
        add(b2);

    }

}
