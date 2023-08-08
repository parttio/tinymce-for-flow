window.Vaadin.Flow.tinymceConnector = {
    initLazy: function (customConfig, c, ta, options) {
        // Check whether the connector was already initialized
        if (c.$connector) {
            return;
        }
        c.$connector = {
          
          setEditorContent : function(html) {
            this.editor.setContent(html);
          },
        
          replaceSelectionContent : function(html) {
            this.editor.selection.setContent(html);
          },
          
          focus : function() {
              this.editor.focus();
          },

          blur : function() {
              this.editor.blur();
          },

          setEnabled : function(enabled) {
              this.editor.mode.set(enabled ? "design" : "readonly");
          }
                  
        };
        
        var currentValue = "";

        const pushChanges = function() {
          c.$server.updateValue(currentValue)
        }

        var baseconfig =  JSON.parse(customConfig) || {}
        
        Object.assign(baseconfig, options);

        baseconfig['suffix'] = '.min';
        baseconfig['promotion'] = false;
        baseconfig['resize'] = false;

        baseconfig['base_url'] = "frontend/tinymce_addon/tinymce/"

        // Height defined in Java component, always just adapt to that
        baseconfig['height'] = "100%";

        baseconfig['target'] = ta;
        
        baseconfig['setup'] = function(ed) {
          c.$connector.editor = ed;
          ed.on('setContent', function(e) {
                currentValue = ed.getContent();
          });
          ed.on('change', function(e) {
                currentValue = ed.getContent();
          });
          ed.on('blur', function(e) {
            currentValue = ed.getContent();
            pushChanges();
            const event = new Event("blur");
            c.dispatchEvent(event);
          });
          ed.on('focus', function(e) {
            const event = new Event("focus");
            c.dispatchEvent(event);
          });

        };

        tinymce.init(baseconfig);

    }
}
