window.Vaadin.Flow.tinymceConnector = {
    initLazy: function (customConfig, c, ta, options) {
        // Check whether the connector was already initialized
        if (c.$connector) {
            return;
        }
        console.log("initLazy");
        c.$connector = {
          
          setEditorContent : function(html) {
            console.log("setEditorContent");
            this.editor.setContent(html);
          },
        
          replaceSelectionContent : function(html) {
            this.editor.selection.setContent(html);
          },
          
          focus : function() {
              this.editor.focus();
          },
          
          setEnabled : function(enabled) {
              console.log("setEnabled");
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
           console.log("setup");
          c.$connector.editor = ed;
          ed.on('setContent', function(e) {
           console.log("setContent");
                currentValue = ed.getContent();
          });
          ed.on('change', function(e) {
              console.log("change");
                currentValue = ed.getContent();
          });
          ed.on('blur', function(e) {
              console.log("blur");
            currentValue = ed.getContent();
            pushChanges();
          });
        };

        tinymce.init(baseconfig);
    }
}
