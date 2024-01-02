package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;

@Route
public class UpdateValueOnDetachedEditor extends VerticalLayout {
    public UpdateValueOnDetachedEditor() {

        TinyMce tinyMce = new TinyMce();

        Button b = new Button("Replace value while detached");
        b.addClickListener(e -> {
            tinyMce.removeFromParent();
            tinyMce.setValue("Now: " + LocalDateTime.now());
            add(tinyMce);
        });

        add(b, tinyMce);

    }
}
