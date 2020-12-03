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
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
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
@Tag("tinymce-editor")
@NpmPackage(value = "@tinymce/tinymce-webcomponent", version = "1.0.2")
@JsModule("@tinymce/tinymce-webcomponent/dist/tinymce-webcomponent.js")
public class TinyMce extends Component implements Field<TinyMce, String>, HasSize {

    private boolean initialContentSent;
    private String currentValue = "";
    private final ValueMapper<String> valueMapper;

    public TinyMce() {
        this.valueMapper = Field.init(this, "", this::setEditorContent);
    }

    public void setEditorContent(String html) {
        this.currentValue = html;
        if (initialContentSent) {
            getElement().executeJs("this._editor.setContent($0)", html);
        } else {
            getElement().setProperty("innerHTML", html);
        }
    }

    @ClientCallable
    private void updateValue(String htmlString) {
        this.currentValue = htmlString;
        valueMapper.setModelValue(currentValue, true);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        injectTinyMceScript();
        // TODO avoid sending value when value not changed and send only a diff
        getElement().executeJs("var el = this;"
                + "this._editor.on('blur', function(evt) {el.$server.updateValue(evt.target.getContent());});"
                + "");
        initConnector();
    }

    /**
     * Injects actual editor script to the host page from the add-on bundle.
     * <p>
     * Override this with an empty implementation if you to use the cloud hosted version, or own custom script if needed.
     */
    protected void injectTinyMceScript() {
        getUI().get().getPage().addJavaScript("tinymce_addon/tinymce/tinymce.js");
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
    }

    public String getCurrentValue() {
        return currentValue;
    }

    @Override
    public void setHeight(String height) {
        HasSize.super.setHeight(height);
        getElement().setAttribute("height", height);
    }

    /**
     * @param contentStyleCss a small set of CSS styles to the editor,
     */
    public void setContentStyle(String contentStyleCss) {
        getElement().setAttribute("content_style", contentStyleCss);
    }

    /**
     * <p>
     * The <code class="language-plaintext highlighter-rouge">toolbar</code>
     * attribute accepts a space-separated string of toolbar buttons with pipe
     * characters (<code class="language-plaintext highlighter-rouge">|</code>)
     * for grouping buttons. For a list of available toolbar buttons, see:
     * <a href="tiny.cloud/docs/advanced/available-toolbar-buttons/">Toolbar
     * Buttons Available for TinyMCE</a>.</p>
     *
     * @param toolbarconfig the configuration
     */
    public void setToolbar(String toolbarconfig) {
        getElement().setAttribute("toolbar", toolbarconfig);
    }

    /**
     * The plugin configuration.
     *
     * @param config the plugin configuration string.
     */
    public void setPlugins(String config) {
        getElement().setAttribute("plugins", config);
    }

    /**
     * Replaces text in the editors selection (can be just a caret position).
     *
     * @param htmlString the html snippet to be inserted
     */
    public void replaceSelectionContent(String htmlString) {
        getElement().executeJs("this._editor.selection.setContent($0)", htmlString);
    }
}
