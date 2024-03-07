package org.vaadin.tinymce;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class AutoSavePluginExample extends VerticalLayout {

    public AutoSavePluginExample() {
        TinyMce tinyMce = new TinyMce();
        tinyMce.configurePlugin(true, Plugin.AUTOSAVE)
                // TODO add constant
                .configure("toolbar", "restoredraft")
                // save more eagerly for demo
                .configure("autosave_interval","5s")
                // Default by tinymce uses id of editor, which happens to be random
                // byfault for this integration
                .configure("autosave_prefix", "tinymce-autosave-myfield-id");
        add(tinyMce);
    }
}
