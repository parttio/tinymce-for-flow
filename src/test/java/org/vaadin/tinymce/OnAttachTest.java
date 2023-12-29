package org.vaadin.tinymce;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

@Route("tinymce-attach")
@PreserveOnRefresh
public class OnAttachTest extends VerticalLayout {

    int i = 0;

    public OnAttachTest() {
        Button button = new Button("Switch");
        List<String> values = Arrays.asList("<b>Value 1</b>", "<i>Value 2</i>",
                "<span style='text-decoration: underline;'>Value 3</span>");
        EditorView editor = new EditorView(values.get(i));
        button.addClickListener(e -> {
            this.removeAll();
            i++;
            if (i > 2)
                i = 0;
            editor.setValue(values.get(i));
            add(button, editor);
        });
        add(button, editor);
    }

    public static class EditorView extends Div {
        private String value;
        private TinyMce tinyMce;
        
        public EditorView(String value) {
            this.value = value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            tinyMce = new TinyMce();
            tinyMce.addValueChangeListener(e -> {
                value = e.getValue();
            });
            tinyMce.configurePlugin(true, Plugin.TABLE).configureToolbar(true,
                    Toolbar.TABLE);
            add(tinyMce);
            tinyMce.setValue(value);
        }

        @Override
        protected void onDetach(DetachEvent detachEvent) {
            remove(tinyMce);
        }
    }
}
