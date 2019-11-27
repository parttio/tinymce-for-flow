package org.vaadin.tinymce;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementConstants;
import com.vaadin.flow.function.SerializableConsumer;
import java.util.UUID;
import org.github.legioth.field.Field;
import org.github.legioth.field.ValueMapper;

@Tag("div")
@JavaScript("https://cdn.tinymce.com/4.9/tinymce.min.js")
@JavaScript("frontend://tinymceConnector.js")
public class TinyMce extends Component implements Field<TinyMce, String>, HasSize {

    private String id;
    private boolean initialContentSent;
    private String currentValue = "";
    private final ValueMapper<String> valueMapper;
    private String config;
    private Element ta = new Element("div");

    public TinyMce() {
        getElement().appendChild(ta);
        this.valueMapper = Field.init(this, "", this::setEditorContent);
    }

    public void setEditorContent(String html) {
        this.currentValue = html;
        if (initialContentSent) {
            runBeforeClientResponse(ui -> getElement()
                    .callFunction("$connector.setEditorContent", html));
        } else {
            ta.setProperty("innerHTML",html);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
    	id = UUID.randomUUID().toString();
        ta.setAttribute("id", id);
        super.onAttach(attachEvent);
        initConnector();
    }
    
    @Override
    protected void onDetach(DetachEvent detachEvent) {
    	super.onDetach(detachEvent);
    	initialContentSent = false;
    	// save the current value to the dom element in case the component gets reattached
    	setEditorContent(currentValue);
    }

    @SuppressWarnings("deprecation")
	private void initConnector() {
        this.initialContentSent = true;
        runBeforeClientResponse(ui -> {
        	ui.getPage().executeJavaScript(
                "window.Vaadin.Flow.tinymceConnector.initLazy($0, $1)", config,
                getElement());
        	});
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
    
    public int getHeaderFooterHeight() {
        return 36+37;
    }
    
    public void setPixelHeight(int pixels) {
        int contentHeight = pixels - getHeaderFooterHeight();
        HasSize.super.setHeight(pixels + "px");
        ta.getStyle().set(ElementConstants.STYLE_HEIGHT, contentHeight + "px");
    }

    @Override
    public void setHeight(String height) {
        HasSize.super.setHeight(height);
        ta.getStyle().set(ElementConstants.STYLE_HEIGHT, height);
    }

    /**
     * Replaces text in the editors selection (can be just a caret position).
     * 
     * @param htmlString the html snippet to be inserted 
     */
	public void replaceSelectionContent(String htmlString) {
        runBeforeClientResponse(ui -> getElement()
                .callFunction("$connector.replaceSelectionContent", htmlString));		
	}

}
