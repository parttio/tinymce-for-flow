window.Vaadin.Flow.tinymceConnector = {
    initLazy: function (customConfig, c) {
        // Check whether the connector was already initialized for the datepicker
        if (c.$connector) {
            return;
        }
        
        c.$connector = {
          
          setEditorContent : function(html) {
            this.editor.setContent(html);
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
        
        baseconfig['selector'] =  "#" + c.id;
        baseconfig['setup'] = function(ed) {
          c.$connector.editor = ed;
          ed.on('setContent', function(e) {
                console.error('Editor content was set');
                currentValue = ed.getContent();
          });
          ed.on('change', function(e) {
                console.error('Editor was changed');
                currentValue = ed.getContent();
          });
          ed.on('blur', function(e) {
            console.error('Editor was blurred');
            currentValue = ed.getContent();
            pushChanges();
          });
        };
        tinymce.init(baseconfig);
    }
}
