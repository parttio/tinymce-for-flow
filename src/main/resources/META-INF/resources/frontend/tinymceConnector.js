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

          setEnabled : function(enabled) {
              this.editor.mode.set(enabled ? "design" : "readonly");
          }
                  
        };
        
        var baseconfig = JSON.parse(customConfig) || {} ;
        
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

          });
          ed.on('change', function(e) {
                // console.log("TMCE change");
                const event = new Event("tchange");
                event.htmlString = ed.getContent();
                c.dispatchEvent(event);
           });
          ed.on('blur', function(e) {
            //console.log("TMCE blur");
            const event = new Event("tblur");
            c.dispatchEvent(event);
          });
          ed.on('focus', function(e) {
            //console.log("TMCE focus");
            const event = new Event("tfocus");
            c.dispatchEvent(event);
          });

          ed.on('input', function(e) {
            //console.log("TMCE input");
            const event = new Event("tchange");
            event.htmlString = ed.getContent();
            c.dispatchEvent(event);
          });

        };

        tinymce.init(baseconfig);

    }
}
