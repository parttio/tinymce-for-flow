package org.vaadin.tinymce;

import java.time.LocalDateTime;

import org.vaadin.tinymce.TinyMce;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class ReplaceSelectionContent extends VerticalLayout {

    public ReplaceSelectionContent() {
    	
    	TinyMce editor = new TinyMce();
    	
    	Button insertCurrentTime = new Button("Insert time");
    	
    	insertCurrentTime.addClickListener(e -> {
    		
    		String now = LocalDateTime.now().toString();
    		
    		editor.replaceSelectionContent(now);
    		
    	});
    	
    	add(editor, insertCurrentTime);
    	

    }
}
