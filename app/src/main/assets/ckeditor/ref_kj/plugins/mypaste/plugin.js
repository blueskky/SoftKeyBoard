(function () {
    'use strict';

    CKEDITOR.plugins.add('mypaste', {
        init: init
    });

    function init(editor) {
        if (editor.addFeature) {
            editor.addFeature({
                allowedContent: 'img[alt,id,!src]{width,height};'
            });
        }

        editor.on("contentDom", function () {
            var editableElement = editor.editable ? editor.editable() : editor.document;
            editableElement.on("paste", onPaste, null, {editor: editor});
        });


    }

    function onPaste(event) {
        var editor = event.listenerData && event.listenerData.editor;		
		bounds.ViewModel.PasteClipboard(JSON.stringify({"callback":"insertImage", "edtId":editor.name}));		
		return false;
    }

    function readImageAsBase64(item, editor) {
        if (!item || typeof item.getAsFile !== 'function') {
            return;
        }

        var file = item.getAsFile();
        var reader = new FileReader();

        reader.onload = function (evt) {
            var element = editor.document.createElement('img', {
                attributes: {
                    src: evt.target.result
                }
            });

            // We use a timeout callback to prevent a bug where insertElement inserts at first caret
            // position
            setTimeout(function () {
                editor.insertElement(element);
            }, 10);
        };

        reader.readAsDataURL(file);
    }
})();
