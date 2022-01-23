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

import java.util.UUID;

import org.github.legioth.field.Field;
import org.github.legioth.field.ValueMapper;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ShadowRoot;
import com.vaadin.flow.function.SerializableConsumer;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

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
@JavaScript("./tinymceConnector.js")
public class TinyMce extends Component implements Field<TinyMce, String>, HasSize {

    private String id;
    private boolean initialContentSent;
    private String currentValue = "";
    private final ValueMapper<String> valueMapper;
    private String rawConfig;
    JsonObject config = Json.createObject();
    private Element ta = new Element("div");

    /**
     * Creates a new TinyMce editor with shadowroot set or disabled. The shadow
     * root should be used if the editor is in used in Dialog component,
     * otherwise menu's and certain other features don't work. On the other
     * hand, the shadow root must not be on when for example used in inline
     * mode.
     * 
     * @param shadowRoot true of shadow root hack should be used
     */
    public TinyMce(boolean shadowRoot) {
        setHeight("500px");
        ta.getStyle().set("height", "100%");
        if(shadowRoot) {
            ShadowRoot shadow = getElement().attachShadow();
            shadow.appendChild(ta);
        } else {
            getElement().appendChild(ta);
        }
        this.valueMapper = Field.init(this, "", this::setEditorContent);
    }
    
    public TinyMce() {
        this(false);
    }

    public void setEditorContent(String html) {
        this.currentValue = html;
        if (initialContentSent) {
            runBeforeClientResponse(ui -> getElement()
                    .callJsFunction("$connector.setEditorContent", html));
        } else {
            ta.setProperty("innerHTML", html);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        id = UUID.randomUUID().toString();
        ta.setAttribute("id", id);
        ta.setProperty("innerHTML", currentValue);
        super.onAttach(attachEvent);
        if(attachEvent.isInitialAttach())
            injectTinyMceScript();
        initConnector();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        initialContentSent = false;
        // save the current value to the dom element in case the component gets reattached
    }

    @SuppressWarnings("deprecation")
    private void initConnector() {
        this.initialContentSent = true;
        
        runBeforeClientResponse(ui -> {
            ui.getPage().executeJs("window.Vaadin.Flow.tinymceConnector.initLazy($0, $1, $2, $3)", rawConfig,
                    getElement(), ta, config);
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
        this.rawConfig = jsonConfig;
    }
    
    public TinyMce configure(String configurationKey, String value) {
    	config.put(configurationKey, value);
        return this;
    }
    
    public TinyMce configure(String configurationKey, String... value) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < value.length; i++) {
            array.set(i, value[i]);
        }
    	config.put(configurationKey, array);
        return this;
    }


    public TinyMce configure(String configurationKey, boolean value) {
    	config.put(configurationKey, value);
        return this;
    }

    public TinyMce configure(String configurationKey, double value) {
    	config.put(configurationKey, value);
        return this;
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
        int majorVersion = com.vaadin.flow.server.Version.getMajorVersion();
        if(majorVersion > 2) {
            getUI().get().getPage().addJavaScript("frontend/tinymce_addon/tinymce/tinymce.js");
        } else {
            getUI().get().getPage().addJavaScript("tinymce_addon/tinymce/tinymce.js");
        }
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
