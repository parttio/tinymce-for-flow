package org.vaadin.tinymce;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.vaadin.firitin.components.RichText;

import java.io.IOException;
import java.nio.charset.Charset;

@Route
public class LargeContentView extends Div {


    protected TinyMce tinyMce;

    public LargeContentView() {
        tinyMce = new TinyMce();

        StringBuilder sb = new StringBuilder();
        sb.append("<div>");

        for (int i = 0; i < 1000; i++) {
            sb.append("<p>This is content p " + i +"</p>");
        }
        sb.append("</div>");

        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.prettyPrint(false);
        String cleaned = Jsoup.clean(sb.toString(), "", Safelist.relaxed(), outputSettings);

        tinyMce.setValue(cleaned);

        tinyMce.setHeight("500px");
        
        add(tinyMce);

        Button b = new Button("Set content dynamically", e -> {
            tinyMce.setEditorContent("New value");
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
            //System.out.println(e.getValue());
        });

    }

}
