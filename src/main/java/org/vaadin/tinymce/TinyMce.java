/*
 * Copyright 2020 Matti Tahvonen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.vaadin.flow.function.SerializableConsumer;
import java.util.UUID;
import org.github.legioth.field.Field;
import org.github.legioth.field.ValueMapper;

/**
 * A Rich Text editor, based on TinyMCE Web Component.
 *
 * Some configurations has Java shorthand, some must be adjusted via
 * getElement().setAttribute(String, String). See full options via
 * https://www.tiny.cloud/docs/integrations/webcomponent/
 *
 * @author mstahv
 */
@Tag("div")
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
            ta.setProperty("innerHTML", html);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        id = UUID.randomUUID().toString();
        ta.setAttribute("id", id);
        super.onAttach(attachEvent);
        injectTinyMceScript();
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

    @Override
    public void setHeight(String height) {
        HasSize.super.setHeight(height);
        runBeforeClientResponse(ui -> getElement()
                .callJsFunction("$connector.setHeight", height));
    }

    /**
     * Replaces text in the editors selection (can be just a caret position).
     *
     * @param htmlString the html snippet to be inserted
     */
    public void replaceSelectionContent(String htmlString) {
        runBeforeClientResponse(ui -> getElement()
                .callJsFunction("$connector.replaceSelectionContent", htmlString));
    }

    /**
     * Injects actual editor script to the host page from the add-on bundle.
     * <p>
     * Override this with an empty implementation if you to use the cloud hosted
     * version, or own custom script if needed.
     */
    protected void injectTinyMceScript() {
        getUI().get().getPage().addJavaScript("tinymce_addon/tinymce/tinymce.js");
    }

    public void focus() {
        runBeforeClientResponse(ui -> getElement()
                .callJsFunction("$connector.focus"));
    }

    @Override
    public void setEnabled(boolean enabled) {
        Field.super.setEnabled(enabled);
        runBeforeClientResponse(ui -> getElement()
                .callJsFunction("$connector.setEnabled", enabled));
    }
    
    @Override
    public void setReadOnly(boolean readOnly) {
        Field.super.setReadOnly(readOnly);
        setEnabled(!readOnly);
    }


}
