package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

@Route
@PreserveOnRefresh
public class PreserveOnRefreshBug27 extends VerticalLayout {

    public PreserveOnRefreshBug27() {
        Dialog dialog = new Dialog();
        TinyMce tinyMce = new TinyMce();
        tinyMce.configure("branding", false);
        tinyMce.configure("statusbar", false);
        tinyMce.setValue("<h2>Hallo Leute,</h2>");
        dialog.add(tinyMce);
        dialog.add(new Button("Cancel", e -> dialog.close()));
        Button open = new Button("Open", e -> dialog.open());
        Button enable = new Button("Disable");
        enable.addClickListener(e -> {
            if ("Disable".equals(enable.getText())) {
                tinyMce.setEnabled(false);
                enable.setText("Enable");
            } else {
                tinyMce.setEnabled(true);
                enable.setText("Disable");
            }
        });
        Button readOnly = new Button("ReadOnly");
        readOnly.addClickListener(e -> {
            if ("ReadOnly".equals(readOnly.getText())) {
                tinyMce.setReadOnly(true);
                readOnly.setText("Writable");
            } else {
                tinyMce.setReadOnly(false);
                readOnly.setText("ReadOnly");
            }
        });
        add(open, enable, readOnly);
    }
}
