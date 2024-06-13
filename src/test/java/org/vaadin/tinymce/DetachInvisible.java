package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class DetachInvisible extends VerticalLayout {

    public DetachInvisible() {
        // https://github.com/parttio/tinymce-for-flow/issues/33
        TinyMce tinyMce = new TinyMce();
        add(tinyMce);
        add(new Button("Toggle visible", e-> tinyMce.setVisible(!tinyMce.isVisible())));
        add(new Button("Detach", e-> tinyMce.removeFromParent()));
    }
}
