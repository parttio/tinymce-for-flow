package org.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route
public class FlowTest extends HorizontalLayout {

    public FlowTest() {
        
        Button b1 = new Button("b1");
        Button b2 = new Button("b2");
        Button b3 = new Button("b3");
        
        TextField textField = new TextField("Voi jorma");
        
        b2.setHeight("100px");
        b3.setHeight("200px");
        
        add(textField, b1,b2,b3);
        
        setVerticalComponentAlignment(Alignment.CENTER, textField, b1,b2,b3);

    }
}
