package org.vaadin.tinymce;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.function.SerializableConsumer;
import java.util.UUID;
import org.github.legioth.field.Field;
import org.github.legioth.field.ValueMapper;

@Tag("div")
@JavaScript("//cdn.tinymce.com/4.6/tinymce.min.js")
@JavaScript("frontend://tinymceConnector.js")
public class TinyMce extends Component implements Field<TinyMce, String> {

    private final String id = UUID.randomUUID().toString();
    private boolean initialContentSent;
    private String currentValue = "";
    private final ValueMapper<String> valueMapper;
    private String config;

    public TinyMce() {
        setId(id);
        this.valueMapper = Field.init(this, "", this::setEditorContent);
    }
    
    public void setEditorContent(String html) {
        this.currentValue = html;
        if(initialContentSent) {
        runBeforeClientResponse(ui -> getElement()
                .callFunction("$connector.setEditorContent", html));
        } else {
            getElement().setProperty("innerHTML", html);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        initConnector();
    }

    private void initConnector() {
        this.initialContentSent = true;
        runBeforeClientResponse(ui -> ui.getPage().executeJavaScript(
                "window.Vaadin.Flow.tinymceConnector.initLazy($0, $1)", config,
                getElement()));
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }

    @ClientCallable
    private void updateValue(String htmlString) {
        this.currentValue = htmlString;
        valueMapper.setModelValue(currentValue, true);
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setConfig(String jsonConfig) {
        this.config = jsonConfig;
    }

}
