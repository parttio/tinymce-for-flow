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

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ShadowRoot;
import com.vaadin.flow.function.SerializableConsumer;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.util.UUID;

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
@JavaScript("context://frontend/tinymce_addon/tinymce/tinymce.js")
public class TinyMce extends AbstractCompositeField<Div, TinyMce, String>
        implements HasSize {

    private String id;
    private boolean initialContentSent;
    private String currentValue = "";
    private String rawConfig;
    JsonObject config = Json.createObject();
    private Element ta = new Element("div");
    private boolean basicTinyMCECreated;

    /**
     * Creates a new TinyMce editor with shadowroot set or disabled. The shadow
     * root should be used if the editor is in used in Dialog component,
     * otherwise menu's and certain other features don't work. On the other
     * hand, the shadow root must not be on when for example used in inline
     * mode.
     * 
     * @param shadowRoot
     *            true of shadow root hack should be used
     */
    public TinyMce(boolean shadowRoot) {
        super("");
        setHeight("500px");
        ta.getStyle().set("height", "100%");
        if (shadowRoot) {
            ShadowRoot shadow = getElement().attachShadow();
            shadow.appendChild(ta);
        } else {
            getElement().appendChild(ta);
        }
    }

    public TinyMce() {
        this(false);
    }

    public void setEditorContent(String html) {
        this.currentValue = html;
        if (initialContentSent) {
            runBeforeClientResponse(ui -> {
                getElement().callJsFunction("$connector.setEditorContent",
                        html);
            });
        } else {
            ta.setProperty("innerHTML", html);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        id = UUID.randomUUID().toString();
        ta.setAttribute("id", id);
        if (!attachEvent.isInitialAttach()) {
            // Value after initial attach should be set via TinyMCE JavaScript
            // API, otherwise value is not updated upon reattach
            initialContentSent = true;
        }
        ta.setProperty("innerHTML", currentValue);
        super.onAttach(attachEvent);
        initConnector();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        initialContentSent = false;
        // save the current value to the dom element in case the component gets
        // reattached
    }

    @SuppressWarnings("deprecation")
    private void initConnector() {

        runBeforeClientResponse(ui -> {
            ui.getPage().executeJs(
                    "window.Vaadin.Flow.tinymceConnector.initLazy($0, $1, $2, $3)",
                    rawConfig, getElement(), ta, config).then(res -> {
                        // Delay setting flag on first attach, otherwise setting
                        // initial value on attach does not work
                        initialContentSent = true;
                    });
        });
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }

    @ClientCallable
    private void updateValue(String htmlString) {
        this.currentValue = htmlString;
        setModelValue(htmlString, true);
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
     * @param htmlString
     *            the html snippet to be inserted
     */
    public void replaceSelectionContent(String htmlString) {
        runBeforeClientResponse(ui -> getElement().callJsFunction(
                "$connector.replaceSelectionContent", htmlString));
    }

    public void focus() {
        runBeforeClientResponse(
                ui -> getElement().callJsFunction("$connector.focus"));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        runBeforeClientResponse(ui -> {
            getElement().callJsFunction("$connector.setEnabled", enabled);
        });
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        setEnabled(!readOnly);
    }

    @Override
    protected void setPresentationValue(String t) {
        setEditorContent(t);
    }

    private TinyMce createBasicTinyMce() {
        this.setEditorContent("");
        this.configure("branding", false);
        this.basicTinyMCECreated = true;
        this.configurePlugin(false, Plugin.ADVLIST, Plugin.AUTOLINK,
                Plugin.LISTS, Plugin.SEARCH_REPLACE);
        this.configureMenubar(false, Menubar.FILE, Menubar.EDIT, Menubar.VIEW,
                Menubar.FORMAT);
        this.configureToolbar(false, Toolbar.UNDO, Toolbar.REDO,
                Toolbar.SEPARATOR, Toolbar.FORMAT_SELECT, Toolbar.SEPARATOR,
                Toolbar.BOLD, Toolbar.ITALIC, Toolbar.SEPARATOR,
                Toolbar.ALIGN_LEFT, Toolbar.ALIGN_CENTER, Toolbar.ALIGN_RIGHT,
                Toolbar.ALIGN_JUSTIFY, Toolbar.SEPARATOR, Toolbar.OUTDENT,
                Toolbar.INDENT);
        return this;

    }

    public TinyMce configurePlugin(boolean basicTinyMCE, Plugin... plugins) {
        if (basicTinyMCE && !basicTinyMCECreated) {
            createBasicTinyMce();
        }

        JsonArray jsonArray = config.get("plugins");
        int initialIndex = 0;

        if (jsonArray != null) {
            initialIndex = jsonArray.length();
        } else {
            jsonArray = Json.createArray();
        }

        for (int i = 0; i < plugins.length; i++) {
            jsonArray.set(initialIndex, plugins[i].pluginLabel);
            initialIndex++;
        }

        config.put("plugins", jsonArray);
        return this;
    }

    public TinyMce configureMenubar(boolean basicTinyMCE, Menubar... menubars) {
        if (basicTinyMCE && !basicTinyMCECreated) {
            createBasicTinyMce();
        }

        JsonArray jsonArray = config.get("menubar");
        int initialIndex = 0;

        if (jsonArray != null) {
            initialIndex = jsonArray.length();
        } else {
            jsonArray = Json.createArray();
        }

        for (int i = 0; i < menubars.length; i++) {
            jsonArray.set(initialIndex, menubars[i].menubarLabel);
            initialIndex++;
        }

        config.put("menubar", jsonArray);
        return this;
    }

    public TinyMce configureToolbar(boolean basicTinyMCE, Toolbar... toolbars) {
        if (basicTinyMCE && !basicTinyMCECreated) {
            createBasicTinyMce();
        }

        JsonValue jsonValue = config.get("toolbar");
        String toolbarStr = "";

        if (jsonValue != null) {
            toolbarStr = toolbarStr.concat(jsonValue.asString());
        }

        for (int i = 0; i < toolbars.length; i++) {
            toolbarStr = toolbarStr.concat(" ").concat(toolbars[i].toolbarLabel)
                    .concat(" ");
        }

        config.put("toolbar", toolbarStr);
        return this;
    }

}
