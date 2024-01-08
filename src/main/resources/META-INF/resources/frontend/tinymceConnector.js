window.Vaadin.Flow.tinymceConnector = {
    initLazy: function (customConfig, c, ta, options) {
        // Check whether the connector was already initialized for the editor
        var currentValue = ta.innerHTML;
        var readonlyTimeout;

        if (c.$connector) {
			// If connector was already set, this is re-attach, remove editor
			// and re-init
			tinymce.remove();
        } else {
          // Init connector at first visit
          c.$connector = {
          
            setEditorContent : function(html) {
			  // Delay setting the content, otherwise there is issue during reattach
              setTimeout(() => {
                currentValue = this.editor.setContent(html, {format : 'html'});
              }, 50);
            },
        
            replaceSelectionContent : function(html) {
              this.editor.selection.setContent(html);
            },
          
            focus : function() {
                this.editor.focus();
            },
          
            setEnabled : function(enabled) {
			  // Debounce is needed if mode is attempted to be changed more than once
			  // during the attach
              readonlyTimeout.clear();
              readonlyTimeout = setTimeout(() => {
                this.editor.mode.set(enabled ? 'design' : 'readonly');
              }, 20);
            }

          };
        
        }

        const pushChanges = function() {
          c.$server.updateValue(currentValue)
        }

        var baseconfig =  JSON.parse(customConfig) || {}
        
        Object.assign(baseconfig, options);
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
          });
        };

        // Allways re-init editor
        tinymce.init(baseconfig);
    }
}
