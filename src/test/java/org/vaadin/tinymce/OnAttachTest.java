package org.vaadin.tinymce;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.internal.ReflectTools;
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
        Button button2 = new Button("Preset");

        EditorView editor = new EditorView(values.get(i));
        button2.addClickListener(e -> {
            editor.setValue("<p style='color: blue'>Blue</p>", true);
        });

        button.addClickListener(e -> {
            this.removeAll();
            i++;
            if (i > 2)
                i = 0;
            editor.setValue(values.get(i), false);
            add(button, button2, editor);
        });
        add(button, button2, editor);
    }

    public static class EditorView extends Composite<TinyMce> {
        private String value;

        public EditorView(String value) {
            this.value = value;
            getContent().setWidth("600px");
            getContent().configureLanguage(Language.FINNISH);
            getContent().setValueChangeMode(ValueChangeMode.BLUR);
            getContent().configurePlugin(true, Plugin.TABLE)
                    .configureToolbar(true, Toolbar.TABLE);
            getContent().addValueChangeListener(e -> {
                this.value = e.getValue();
                Notification.show(this.value);
            });
            getContent().setEnabled(false);
        }

        public void setValue(String value, boolean immediate) {
            if (immediate) {
                Notification.show(getContent().getValue());
                getContent().setValue(value);
            }
            this.value = value;
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            getContent().setEnabled(true);
            getContent().setValue(value);
        }

        @Override
        protected void onDetach(DetachEvent detachEvent) {
            getContent().setEnabled(false);
        }
    }
}