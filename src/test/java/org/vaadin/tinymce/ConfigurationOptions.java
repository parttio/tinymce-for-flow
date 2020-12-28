package org.vaadin.tinymce;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.router.Route;

@Route
public class ConfigurationOptions extends Div {

    public ConfigurationOptions() {
        add(new H1("Couple of examples with various init configurations"));
        addWithConfig()
                .configure("branding", false)
                .configure("plugins", "link");
        addWithConfig()
                .configure("inline", true)
                        .configure("plugins", "link").setHeight(null);;

        addWithConfig()
                .configure("plugins", "quickbars", "link")
                .configure("toolbar", false)
                .configure("menubar", false)
                .configure("inline", true);
        addWithConfig()
                .configure("sking", "oxide-dark")
                .configure("content_css", "dark");

    }

    private TinyMce addWithConfig() {
        TinyMce tinyMce = new TinyMce();
        tinyMce.setValue("Initial <em>content</em>");
        final Pre pre = new Pre();
        add(pre);
        add(tinyMce);
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> {
                    pre.setText(tinyMce.config.toJson());
                }));
        return tinyMce;
    }

}
