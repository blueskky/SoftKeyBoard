CKEDITOR.plugins.add('upload_image', {
    icons: 'upload_image',
    init: function (editor) {
        editor.addCommand('uploadImage', {
            exec: function (editor) {				
				bounds.ViewModel.UploadImage(JSON.stringify({"callback":"insertImage", "edtId":editor.name}));
            }
        });
        editor.ui.addButton('upload_image', {
            label: '&#x4E0A;&#x4F20;&#x56FE;&#x7247;',
            command: 'uploadImage',
            toolbar: 'insert, 101',
        });        
    }
});