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
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ShadowRoot;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

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
@CssImport("./tinymceLumo.css")
public class TinyMce extends AbstractCompositeField<Div, TinyMce, String>
        implements HasSize, Focusable<TinyMce> {

    private final DomListenerRegistration domListenerRegistration;
    private String id;
    private boolean initialContentSent;
    private String currentValue = "";
    private String rawConfig;
    JsonObject config = Json.createObject();
    private Element ta = new Element("div");
    private boolean basicTinyMCECreated;
    private boolean enabled = true;
    private boolean readOnly = false;
    private int debounceTimeout = 0;

    /**
     * Creates a new TinyMce editor with shadowroot set or disabled. The shadow
     * root should be used if the editor is in used in Dialog component,
     * otherwise menu's and certain other features don't work. On the other
     * hand, the shadow root must not be on when for example used in inline
     * mode.
     * 
     * @deprecated No longer needed since version x.x
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
        getElement().getClassList().add("tinymce-flow");
        domListenerRegistration = getElement().addEventListener("tchange",
                (DomEventListener) event -> {
                    boolean value = event.getEventData()
                            .hasKey("event.htmlString");
                    String htmlString = event.getEventData()
                            .getString("event.htmlString");
                    currentValue = htmlString;
                    setModelValue(htmlString, true);
                });
        domListenerRegistration.addEventData("event.htmlString");
        domListenerRegistration.debounce(debounceTimeout);
    }

    /**
     * Define the mode of value change triggering. BLUR: Value is triggered only
     * when TinyMce loses focus, TIMEOUT: TinyMce will send value change eagerly
     * but debounced with timeout, CHANGE: value change is sent when TinyMce
     * emits change event (e.g. enter, tab)
     *
     * @see setDebounceTimeout(int)
     * @param mode
     *            The mode.
     */
    public void setValueChangeMode(ValueChangeMode mode) {
        if (mode == ValueChangeMode.BLUR) {
            runBeforeClientResponse(ui -> {
                getElement().callJsFunction("$connector.setMode", "blur");
            });
        } else if (mode == ValueChangeMode.TIMEOUT) {
            runBeforeClientResponse(ui -> {
                getElement().callJsFunction("$connector.setMode", "timeout");
            });
        } else if (mode == ValueChangeMode.CHANGE) {
            runBeforeClientResponse(ui -> {
                getElement().callJsFunction("$connector.setMode", "change");
            });
        }
    }

    /**
     * Sets the debounce timeout for the value change event. The default is 0,
     * when value change is triggered on blur and enter key presses. When value
     * is more than 0 the value change is emitted with delay of given timeout
     * milliseconds after last keystroke.
     *
     * @see setValueChangeMode(ValueChangeMode)
     * @param debounceTimeout
     *            the debounce timeout in milliseconds
     */
    public void setDebounceTimeout(int debounceTimeout) {
        if (debounceTimeout > 0) {
            runBeforeClientResponse(ui -> {
                getElement().callJsFunction("$connector.setEager", "timeout");
            });
        } else {
            runBeforeClientResponse(ui -> {
                getElement().callJsFunction("$connector.setEager", "change");
            });
        }
        this.debounceTimeout = debounceTimeout;
        domListenerRegistration.debounce(debounceTimeout);
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
        if (id == null) {
            id = UUID.randomUUID().toString();
            ta.setAttribute("id", id);
        }
        if (!getEventBus().hasListener(BlurEvent.class)) {
            // adding fake blur listener so throttled value
            // change events happen by latest at blur
            addBlurListener(e -> {
            });
        }
        if (!attachEvent.isInitialAttach()) {
            // Value after initial attach should be set via TinyMCE JavaScript
            // API, otherwise value is not updated upon reattach
            initialContentSent = true;
        }
        ta.setProperty("innerHTML", currentValue);
        super.onAttach(attachEvent);
        initConnector();
        saveOnClose();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        detachEvent.getUI().getPage().executeJs("tinymce.get($0).remove();",
                id);
        super.onDetach(detachEvent);
        initialContentSent = false;
        // save the current value to the dom element in case the component gets
        // reattached
    }

    @SuppressWarnings("deprecation")
    private void initConnector() {

        runBeforeClientResponse(ui -> {
            ui.getPage().executeJs(
                    "window.Vaadin.Flow.tinymceConnector.initLazy($0, $1, $2, $3, $4, $5)",
                    rawConfig, getElement(), ta, config, currentValue,
                    (enabled && !readOnly)).then(res -> {
                        // Delay setting flag on first attach, otherwise setting
                        // initial value on attach does not work
                        initialContentSent = true;
                    });
        });
    }

    private void saveOnClose(){
        runBeforeClientResponse(ui -> {
            getElement().callJsFunction("$connector.saveOnClose");});
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
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

    public TinyMce configureLanguage(Language language) {
        config.put("language", language.toString());
        ;
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

    @Override
    public void focus() {
        runBeforeClientResponse(ui -> {
            // Dialog has timing issues...
            getElement().executeJs(
            //@formatter:off
                        "const el = this;"+
                        "if($connector.isInDialog()) {"+
                        "    setTimeout(() => {"+
                        "        $connector.focus()"+
                        "    }, 150);"+
                        "} else {"+
                        "    $connetor.focus();"+
                        "}"
            //@formatter:on
            );
        });
    }

    @Override
    public Registration addFocusListener(
            ComponentEventListener<FocusEvent<TinyMce>> listener) {
        DomListenerRegistration domListenerRegistration = getElement()
                .addEventListener("tfocus", event -> listener
                        .onComponentEvent(new FocusEvent<>(this, false)));
        return domListenerRegistration;
    }

    @Override
    public Registration addBlurListener(
            ComponentEventListener<BlurEvent<TinyMce>> listener) {
        DomListenerRegistration domListenerRegistration = getElement()
                .addEventListener("tblur", event -> listener
                        .onComponentEvent(new BlurEvent<>(this, false)));
        return domListenerRegistration;
    }

    @Override
    public void blur() {
        throw new RuntimeException(
                "Not implemented, TinyMce does not support programmatic blur.");
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        adjustEnabledState();
    }

    private void adjustEnabledState() {
        boolean reallyEnabled = this.enabled && !this.readOnly;
        super.setEnabled(reallyEnabled);
        runBeforeClientResponse(ui -> getElement()
                .callJsFunction("$connector.setEnabled", reallyEnabled));
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        super.setReadOnly(readOnly);
        adjustEnabledState();
    }

    @Override
    protected void setPresentationValue(String html) {
        this.currentValue = html;
        if (initialContentSent) {
            runBeforeClientResponse(ui -> getElement()
                    .callJsFunction("$connector.setEditorContent", html));
        }
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
