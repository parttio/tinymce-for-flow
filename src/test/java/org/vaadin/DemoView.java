package org.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.tinymce.TinyMce;

@Route
public class DemoView extends Div {
    

    protected TinyMce tinyMce;

    public DemoView() {
        tinyMce = new TinyMce();
        // Full list of options: https://www.tiny.cloud/docs/integrations/webcomponent/
        tinyMce.setPlugins("advlist autolink lists link image charmap print preview anchor\n" +
"      searchreplace visualblocks code fullscreen\n" +
"      insertdatetime media table paste code help wordcount");
        tinyMce.setToolbar("undo redo | formatselect | bold italic backcolor |\n" +
"      alignleft aligncenter alignright alignjustify |\n" +
"      bullist numlist outdent indent | removeformat | help");
        tinyMce.setContentStyle("body\n" +
"      {\n" +
"        font-family:Helvetica,Arial,sans-serif;\n" +
"        font-size:14px; color:blue;\n" +
"      }");

        tinyMce.setEditorContent("<p>Voi <strong>jorma</strong>!<p>");
        tinyMce.setHeight("700px");
        add(tinyMce);

        Button b = new Button("Set content dynamically", e -> {
            tinyMce.setEditorContent("New value");
        });
        add(b);

        Button b2 = new Button("Show content", e -> {
            Notification.show(tinyMce.getCurrentValue());
        });
        add(b2);
        
        Button focus = new Button("focus", e->{
            tinyMce.getElement().executeJs("focus()");
        });
        add(focus);

        tinyMce.addValueChangeListener(e -> {
            Notification.show("ValueChange event!");
            System.out.println(e.getValue());
        });

    }

}
