package org.vaadin.tinymce;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class TinyMceWithAdditionalConfig extends VerticalLayout {

    public TinyMceWithAdditionalConfig() {
        TinyMce tinyMce = new TinyMce();
        tinyMce.configurePlugin(true, Plugin.TABLE)
                .configureMenubar(true, Menubar.TABLE)
                .configureToolbar(true, Toolbar.TABLE);
        add(tinyMce);
    }
}
