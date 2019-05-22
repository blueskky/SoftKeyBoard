CKEDITOR.plugins.add('capture_screen', {
    icons: 'CaptureScreen',
    init: function (editor) {
        editor.addCommand('captureScreen', {
            exec: function (editor) {
				bounds.ViewModel.CaptureScreen(JSON.stringify({"callback":"insertImage", "edtId":editor.name}));
            }
        });
        editor.ui.addButton('CaptureScreen', {
            label: '&#x622A;&#x53D6;&#x56FE;&#x7247;',
            command: 'captureScreen',
            toolbar: 'insert, 100',
        });        
    }
});