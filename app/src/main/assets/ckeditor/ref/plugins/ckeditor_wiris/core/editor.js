//
//  Copyright (c) 2011, Maths for More S.L. http://www.wiris.com
//  This file is part of WIRIS Plugin.
//
//  WIRIS Plugin is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  any later version.
//
//  WIRIS Plugin is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with WIRIS Plugin. If not, see <http://www.gnu.org/licenses/>.
//

var wrs_int_opener;
var closeFunction;

if (window.opener) {							// For popup mode.
	wrs_int_opener = window.opener;
	closeFunction = window.close;
}
/* FCKeditor integration begin */
else {											// For iframe mode.
	wrs_int_opener = window.parent;
	
	while (wrs_int_opener.InnerDialogLoaded) {
		wrs_int_opener = wrs_int_opener.parent;
	}
}

if (window.parent.InnerDialogLoaded) {			// iframe mode.
	window.parent.InnerDialogLoaded();
	closeFunction = window.parent.Cancel;
}
else if (window.opener.parent.FCKeditorAPI) {	// popup mode.
	wrs_int_opener = window.opener.parent;
}
/* FCKeditor integration end */

wrs_int_opener.wrs_addEvent(window, 'load', function () {
	var applet = document.getElementById('applet');
	
	// Mathml content.
	if (!wrs_int_opener._wrs_isNewElement) {
		var mathml;
		var attributeValue = wrs_int_opener._wrs_temporalImage.getAttribute(wrs_int_opener._wrs_conf_imageMathmlAttribute);
			
		if (attributeValue == null) {
			attributeValue = wrs_int_opener._wrs_temporalImage.getAttribute('alt');
		}
		
		if (wrs_int_opener._wrs_conf_useDigestInsteadOfMathml) {		
			mathml = wrs_int_opener.wrs_getCode(wrs_int_opener._wrs_conf_digestPostVariable, attributeValue);
		}
		else {
			mathml = wrs_int_opener.wrs_mathmlDecode(attributeValue);
		}
		
		function setAppletMathml() {
			// Internet explorer fails on "applet.isActive". It only supports "applet.isActive()".
			
			try {
				if (applet.isActive()) {
					applet.setContent(mathml);
				}
				else {
					setTimeout(setAppletMathml, 50);
				}
			}
			catch (e) {
				setTimeout(setAppletMathml, 50);
			}
		}

		setAppletMathml();
	}
	
	// Submit button.
	var controls = document.getElementById('controls');
	var submitButton = document.createElement('input');
	submitButton.type = 'button';
	if (strings['accept'] != null){
		submitButton.value = strings['accept'];
	}else{
		submitButton.value = 'Accept';
	}
	
	wrs_int_opener.wrs_addEvent(submitButton, 'click', function () {
		//Used to close WIRIS editor if the main editor doesn't exist anymore.
		if (!('wrs_int_updateFormula' in wrs_int_opener)){
			window.close();
			return;
		}
		
		var mathml = '';
	
		if (!applet.isFormulaEmpty()) {
			mathml += applet.getContent();							// If isn't empty, get mathml code to mathml variable.
			mathml = wrs_int_opener.wrs_mathmlEntities(mathml);		// Apply a parse.
		}
		
		var queryParams = wrs_int_opener.wrs_getQueryParams(window);
		
		/* FCKeditor integration begin */
		if (window.parent.InnerDialogLoaded && window.parent.FCKBrowserInfo.IsIE) {			// On IE, we must close the dialog for push the caret on the correct position.
			closeFunction();
			wrs_int_opener.wrs_int_updateFormula(mathml, wrs_int_opener._wrs_editMode, queryParams['lang']);
		}
		/* FCKeditor integration end */
		else {
			if (wrs_int_opener.wrs_int_updateFormula) {
				wrs_int_opener.wrs_int_updateFormula(mathml, wrs_int_opener._wrs_editMode, queryParams['lang']);
			}
			
			closeFunction();
		}
	});
	
	controls.appendChild(submitButton);

	// Cancel button.
	var cancelButton = document.createElement('input');
	cancelButton.type = 'button';
	if (strings['cancel'] != null){
		cancelButton.value = strings['cancel'];
	}else{
		cancelButton.value = 'Cancel';
	}
	
	wrs_int_opener.wrs_addEvent(cancelButton, 'click', function () {
		closeFunction();
	});
	
	controls.appendChild(cancelButton);

	var manualLink = document.getElementById('a_manual');
	if (strings['manual'] != null){
		manualLink.innerHTML = strings['manual'];
	}
	
	// Auto resizing.
	
	setInterval(function () {
		applet.style.height = (document.getElementById('container').offsetHeight - controls.offsetHeight - 10) + 'px';
	}, 100);
});

wrs_int_opener.wrs_addEvent(window, 'unload', function () {
	wrs_int_opener.wrs_int_notifyWindowClosed();
});
