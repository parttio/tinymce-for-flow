window.Vaadin.Flow.tinymceConnector = {
    initLazy: function (customConfig, c, ta, options, initialContent, enabled) {
		var currentValue = ta.innerHTML;
        var readonlyTimeout;
 
        // Check whether the connector was already initialized
        if (c.$connector) {
            // If connector was already set, this is re-attach, remove editor
            // and re-init
            c.$connector.editor.remove();
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
              clearTimeout(readonlyTimeout);
              readonlyTimeout = setTimeout(() => {
                this.editor.mode.set(enabled ? 'design' : 'readonly');
              }, 20);
            }
                  
          };
        }
        
        var baseconfig = JSON.parse(customConfig) || {} ;
        
        Object.assign(baseconfig, options);

        baseconfig['suffix'] = '.min';
        baseconfig['promotion'] = false;
        baseconfig['resize'] = false;

        // Height defined in Java component, always just adapt to that
        baseconfig['height'] = "100%";

        baseconfig['target'] = ta;

        if(!enabled) {
            baseconfig['readonly'] = true;
        }
        
        baseconfig['setup'] = function(ed) {
          c.$connector.editor = ed;

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

        ta.innerHTML = initialContent;

        // Move aux element as child to fix Dialog issues, TinyMCE is slow
        // to init, thus timeout needed        
        setTimeout(() => {
          const aux = document.getElementsByClassName('tox-tinymce-aux')[0];
          aux.parentElement.removeChild(aux);
          // Fix to allow menu grow outside Dialog
          aux.style.position = 'absolute';
          c.appendChild(aux);
        }, 500);

        tinymce.init(baseconfig);

    }
}
