package org.vaadin.tinymce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;

@Route
public class TinyMceWithVaadinSelect extends VerticalLayout {
    
    public enum Options {
        FOO,BAR
    }

    public TinyMceWithVaadinSelect() {
        
        TinyMce tiny = new TinyMce();
        tiny.setValue("Edit me");
        
        Select<Options> select = new Select<>();
        select.setItems(Options.values());
        select.setValue(Options.FOO);
        
        Button b = new Button("Set value and toggle visibility");
        b.addClickListener(e -> {
            select.setValue(Options.BAR);
            if(tiny.isAttached()) {
                remove(select);
                remove(tiny);
            } else {
                add(tiny);
                add(select);
            }
            
        });
        add(b);
    }
}
