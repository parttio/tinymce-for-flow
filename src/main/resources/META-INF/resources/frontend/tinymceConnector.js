window.Vaadin.Flow.tinymceConnector = {
    initLazy: function (customConfig, c) {
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
          },
          
          setHeight : function(h) {
              this.editor.settings.height = h;
          }
        
        };
        
        var currentValue = "";

        const pushChanges = function() {
          c.$server.updateValue(currentValue)
        }

        var baseconfig =  JSON.parse(customConfig) || {
          height: 500,
          plugins: [
            'advlist autolink lists link image charmap print preview anchor',
            'searchreplace visualblocks code fullscreen',
            'insertdatetime media table contextmenu paste code'
          ],
          toolbar: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image',
        }
        baseconfig['selector'] =  "#" + c.firstChild.id;
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
