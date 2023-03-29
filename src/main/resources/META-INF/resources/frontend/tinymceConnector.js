window.Vaadin.Flow.tinymceConnector = {
    initLazy: function (customConfig, c, ta, options) {
        // Check whether the connector was already initialized for the datepicker
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
        // Height defined in Java component, always just adapt to that
        baseconfig['height'] = "100%";

        baseconfig['target'] = ta;

        baseconfig['base_url'] = "frontend/tinymce_addon/tinymce/"

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
          });
        };

        tinymce.init(baseconfig);
    }
}
