package org.vaadin.tinymce;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;

@Route
@PreserveOnRefresh
public class PreserveOnRefreshBug27 extends VerticalLayout {

    public PreserveOnRefreshBug27() {
        Dialog dialog = new Dialog();
        TinyMce tinyMce = new TinyMce();
        tinyMce.setTabIndex(1);
        tinyMce.configure("branding", false);
        tinyMce.configure("statusbar", false);
        tinyMce.setValue("<h2>Hallo Leute,</h2>");
        dialog.add(tinyMce);
        dialog.add(new VButton("Cancel", e -> dialog.close()).withTabIndex(3));
        VButton button = new VButton("Focus (CTRL-I)", e -> tinyMce.focus());
        button.setTabIndex(2);
        button.addClickShortcut(Key.of("i"), KeyModifier.CONTROL);
        dialog.add(button);
        Button open = new Button("Open", e -> {dialog.open(); tinyMce.focus();});
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
