package org.vaadin;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import org.vaadin.tinymce.TinyMce;

@Route
public class ConfigurationOptions extends Div {

    public ConfigurationOptions() {
        add(new H1("Couple of examples with various init configurations"));
        addWithConfig("{\"branding\": false}");
        addWithConfig("{\"inline\": true}");
        addWithConfig("{    \"plugins\": [ \"quickbars\" ],\n"
                + "    \"toolbar\": false,\n"
                + "    \"menubar\": false,\n"
                + "    \"inline\": true}");
        addWithConfig("{\"skin\": \"oxide-dark\",\n"
                + "  \"content_css\": \"dark\"}");

    }

    private void addWithConfig(String config) {
        TinyMce tinyMce = new TinyMce();
        tinyMce.setValue("Initial <em>content</em>");
        tinyMce.setConfig(config);
        add(new Pre(config));
        add(tinyMce);
    }

}
