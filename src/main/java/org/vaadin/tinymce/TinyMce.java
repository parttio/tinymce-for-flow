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

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ShadowRoot;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A Rich Text editor, based on TinyMCE Web Component.
 * <p>
 * Some configurations has Java shorthand, some must be adjusted via
 * getElement().setAttribute(String, String). See full options via
 * https://www.tiny.cloud/docs/integrations/webcomponent/
 *
 * @author mstahv
 */
@Tag("div")
@JavaScript("./tinymceConnector.js")
public class TinyMce extends AbstractCompositeField<Div, TinyMce, String> implements HasSize, Focusable<TinyMce> {

    private final DomListenerRegistration domListenerRegistration;
    private String id;
    private boolean initialContentSent;
    private String currentValue = "";
    private String diffBase = currentValue;
    private String rawConfig;
    JsonObject config = Json.createObject();
    private Element ta = new Element("div");

    private int debounceTimeout = 2000;

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
        super("");
        setHeight("500px");
        ta.getStyle().set("height", "100%");
        if (shadowRoot) {
            ShadowRoot shadow = getElement().attachShadow();
            shadow.appendChild(ta);
        } else {
            getElement().appendChild(ta);
        }
        domListenerRegistration = getElement().addEventListener("tchange", (DomEventListener) event -> {
            boolean value = event.getEventData().hasKey("event.htmlString");
            if(event.getEventData().hasKey("event.patch_text")) {
                String patchText = event.getEventData().getString("event.patch_text");

                System.out.println(patchText);

                DiffMatchPatch dmp = new DiffMatchPatch();

                LinkedList<DiffMatchPatch.Patch> patches = (LinkedList<DiffMatchPatch.Patch>) dmp.patchFromText(patchText);
                Object[] objects = dmp.patchApply(patches, currentValue);
                String newValueViaDiff = objects[0].toString();
                System.out.println(newValueViaDiff.length());

                currentValue = newValueViaDiff;
                setModelValue(newValueViaDiff, true);
                //System.out.println(newValueViaDiff);

                // TODO figure out when it makes sense to try to change
                // the "diffBase". If diffs grow to be so large they
                // overweight the chattiness coming from akno messages
                if(true) {
                    acknowledge((int) event.getEventData().getNumber("event.idx"));
                }
            }

            if(value) {
                String htmlString = event.getEventData().getString("event.htmlString");
                currentValue = htmlString;
                setModelValue(htmlString, true);
            } else {
                // diff transferred
            }
        });
        domListenerRegistration.addEventData("event.patch_text");
        domListenerRegistration.addEventData("event.idx");
        domListenerRegistration.debounce(debounceTimeout);
    }

    private void acknowledge(int id) {
        getElement().executeJs("this.$connector.acknowledge", id).then(r -> {
            String string = r.asString();
            if("ok".equals(string)) {
                diffBase = currentValue;
            }
        });
    }

    /**
     * Sets the debounce timeout for the value change event. The default is 5000.
     * @param debounceTimeout the debounce timeout in milliseconds
     */
    public void setDebounceTimeout(int debounceTimeout) {
        this.debounceTimeout = debounceTimeout;
    }

    public TinyMce() {
        this(false);
    }

    public void setEditorContent(String html) {
        this.currentValue = html;
        if (initialContentSent) {
            runBeforeClientResponse(ui -> getElement()
                    .callJsFunction("$connector.setEditorContent", html));
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        id = UUID.randomUUID().toString();
        ta.setAttribute("id", id);
        ta.setProperty("innerHTML", currentValue);
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach())
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
            ui.getPage().executeJs("window.Vaadin.Flow.tinymceConnector.initLazy($0, $1, $2, $3, $4)", rawConfig,
                    getElement(), ta, config, currentValue);

        });
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> {
            ui.beforeClientResponse(this, context -> command.accept(ui));
            diffBase = currentValue;
        });
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
        getUI().get().getPage().addJavaScript("frontend/tinymce_addon/tinymce/tinymce.min.js");
    }

    @Override
    public void focus() {
        runBeforeClientResponse(ui -> getElement()
                .callJsFunction("$connector.focus"));
    }

    @Override
    public Registration addFocusListener(ComponentEventListener<FocusEvent<TinyMce>> listener) {
        DomListenerRegistration domListenerRegistration = getElement().addEventListener("tfocus", event -> listener.onComponentEvent(new FocusEvent<>(this, false)));
        return domListenerRegistration;
    }

    @Override
    public Registration addBlurListener(ComponentEventListener<BlurEvent<TinyMce>> listener) {
        DomListenerRegistration domListenerRegistration = getElement().addEventListener("tblur", event -> listener.onComponentEvent(new BlurEvent<>(this, false)));
        return domListenerRegistration;
    }

    @Override
    public void blur() {
        throw new RuntimeException("Not implemented, TinyMce does not support programmatic blur.");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        runBeforeClientResponse(ui -> getElement()
                .callJsFunction("$connector.setEnabled", enabled));
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


}
