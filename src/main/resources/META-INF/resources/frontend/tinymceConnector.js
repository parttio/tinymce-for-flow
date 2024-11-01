window.Vaadin.Flow.tinymceConnector = {
    initLazy: function (customConfig, c, ta, options, initialContent, enabled) {
		var currentValue = ta.innerHTML;
        var readonlyTimeout;
        var changeMode;
        
        const beforeUnloadHandler = (event) => {
          const blurEvent = new Event("tblur");
          c.dispatchEvent(blurEvent);
          const changeEvent = new Event("tchange");
          changeEvent.htmlString = c.$connector.editor.getContent();
          c.dispatchEvent(changeEvent);
        };

        window.removeEventListener("beforeunload", beforeUnloadHandler);
 
        // Check whether the connector was already initialized
        if (c.$connector) {
            // If connector was already set, this is re-attach, remove editor
            // and re-init
            c.$connector.editor.remove();
        } else {
          // Init connector at first visit
          changeMode = 'change';
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
            },

            setMode : function(newChangeMode) {
              changeMode = newChangeMode;
            },
	
			isInDialog: function() {
                let inDialog = false;
                let parent = c.parentElement;
                while(parent != null) {
                    if(parent.tagName.toLowerCase().indexOf("vaadin-dialog") == 0) {
                        inDialog = true;
                        break;
                    }
                    parent = parent.parentElement;
                }
                return inDialog;
            },
            
            saveOnClose : function() {
                window.addEventListener("beforeunload", beforeUnloadHandler);
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

            ed.on('init', e => {
                if(c.$connector.isInDialog()) {
                    // This is inside a shadowroot (Dialog in Vaadin)
                    // and needs some hacks to make this nagigateable with keyboard
                    if(c.tabIndex < 0) {
                        // make the wrapping element also focusable
                        c.setAttribute("tabindex", 0);
                    }
                    // on focus to wrapping element, pass forward to editor
                    c.addEventListener("focus", e=> {
                      ed.focus();
                    });
                    // Move aux element as child from body to element to fix menus in modal Dialog
                    Array.from(document.getElementsByClassName('tox-tinymce-aux')).forEach(aux => {
					  if (!aux.dontmove) {
                        aux.parentElement.removeChild(aux);
                        // Fix to allow menu grow outside Dialog
                        aux.style.position = 'absolute';
                        c.editor = ed;
                        c.appendChild(aux);
                      }
                    });
                } else {
					const aux = document.getElementsByClassName('tox-tinymce-aux')[0];
					aux.dontmove = true;
				}
            });

            ed.on('change', function(e) {
			  if (changeMode === 'timeout') {
                const event = new Event("tchange");
                event.htmlString = ed.getContent();
                c.dispatchEvent(event);
              }
            });
            ed.on('blur', function(e) {
              const blurEvent = new Event("tblur");
              c.dispatchEvent(blurEvent);
              const changeEvent = new Event("tchange");
              changeEvent.htmlString = ed.getContent();
              c.dispatchEvent(changeEvent);
            });
            ed.on('focus', function(e) {
              const event = new Event("tfocus");
              c.dispatchEvent(event);
            });

            ed.on('input', function(e) {
              if (changeMode === 'timeout') {
                const event = new Event("tchange");
                event.htmlString = ed.getContent();
                c.dispatchEvent(event);
              }
            });

        };

        ta.innerHTML = initialContent;

        tinymce.init(baseconfig);

    }
}
