// Including core.js
var script = document.createElement('script');
script.type = 'text/javascript';
script.src = CKEDITOR.basePath + '/plugins/ckeditor_wiris/core/core.js';
document.getElementsByTagName('head')[0].appendChild(script);

// Configuration
var _wrs_conf_editorEnabled = true;		// Specifies if fomula editor is enabled.
var _wrs_conf_CASEnabled = false;		// Specifies if WIRIS cas is enabled.

var _wrs_conf_imageMathmlAttribute = 'data-mathml';	// Specifies the image tag where we should save the formula editor mathml code.
var _wrs_conf_CASMathmlAttribute = 'alt';	// Specifies the image tag where we should save the WIRIS cas mathml code.

var _wrs_conf_editorPath = 'file://C:/TyEdu/data/ExerciseRes/ckeditor/editor_engine/editor.html';        // Specifies where is the editor HTML code (for popup window).
var _wrs_conf_editorAttributes = 'width=570, height=450, top=45, left=10, scroll=no, resizable=no';				// Specifies formula editor window options.
var _wrs_conf_CASPath = 'http://kc.huijiaoyun.cn/editor_engine/cas';        // _wrs_conf_CASPath = '/pluginwiris_engine/app/cas';        // _wrs_conf_CASPath = '/pluginwiris_engine/app/cas';        // _wrs_conf_CASPath = CKEDITOR.basePath + '/plugins/ckeditor_wiris/integration/cas.php';					// Specifies where is the WIRIS cas HTML code (for popup window).
var _wrs_conf_CASAttributes = 'width=640, height=480, scroll=no, resizable=yes';							// Specifies WIRIS cas window options.

var _wrs_conf_createimagePath = 'http://kc.huijiaoyun.cn/editor_engine/createimage';        // _wrs_conf_createimagePath = '/pluginwiris_engine/app/createimage';        // _wrs_conf_createimagePath = '/pluginwiris_engine/app/createimage';        // _wrs_conf_createimagePath = CKEDITOR.basePath + '/plugins/ckeditor_wiris/integration/createimage.php';			// Specifies where is createimage script.
var _wrs_conf_createcasimagePath = 'http://kc.huijiaoyun.cn/editor_engine/createcasimage';        // _wrs_conf_createcasimagePath = '/pluginwiris_engine/app/createcasimage';        // _wrs_conf_createcasimagePath = '/pluginwiris_engine/app/createcasimage';        // _wrs_conf_createcasimagePath = CKEDITOR.basePath + '/plugins/ckeditor_wiris/integration/createcasimage.php';	// Specifies where is createcasimage script.

var _wrs_conf_getmathmlPath = 'http://kc.huijiaoyun.cn/editor_engine/getmathml';        // _wrs_conf_getmathmlPath = '/pluginwiris_engine/app/getmathml';        // _wrs_conf_getmathmlPath = '/pluginwiris_engine/app/getmathml';        // _wrs_conf_getmathmlPath = CKEDITOR.basePath + '/plugins/ckeditor_wiris/integration/getmathml.php';			// Specifies where is the getmathml script.
var _wrs_conf_servicePath = 'http://kc.huijiaoyun.cn/editor_engine/service';        // _wrs_conf_servicePath = '/pluginwiris_engine/app/service';        // _wrs_conf_servicePath = '/pluginwiris_engine/app/service';        // _wrs_conf_servicePath = CKEDITOR.basePath + '/plugins/ckeditor_wiris/integration/service.php';			// Specifies where is the service script.

var _wrs_conf_saveMode = 'tags';					// This value can be 'tags', 'xml' or 'safeXml'.
var _wrs_conf_parseModes = ['latex'];				// This value can contain 'latex'.

var _wrs_conf_enableAccessibility = true;

// Vars
var _wrs_int_editorIcon = CKEDITOR.basePath + '/plugins/ckeditor_wiris/core/icons/formula.gif';
var _wrs_int_CASIcon = CKEDITOR.basePath + '/plugins/ckeditor_wiris/core/icons/cas.gif';
var _wrs_int_temporalElement;
var _wrs_int_temporalElementIsIframe;
var _wrs_int_window;
var _wrs_int_window_opened = false;
var _wrs_int_temporalImageResizing;
var _wrs_int_wirisProperties;
var _wrs_int_directionality;

// Plugin integration
CKEDITOR.plugins.add('ckeditor_wiris', {
	'init': function (editor) {
		/*
		 * Fix for a bug when there is more than one editor in the same page.
		 * It removes wiris element from config array when more than one is found.
		 */
		var _wrs_toolbarName = 'toolbar_' + editor.config.toolbar;
		 
		if (CKEDITOR.config[_wrs_toolbarName] != null) {
			var wirisButtonIncluded = false;
			
			for (var i = 0; i < CKEDITOR.config[_wrs_toolbarName].length; ++i) {
				if (CKEDITOR.config[_wrs_toolbarName][i].name == 'wiris') {
					if (!wirisButtonIncluded) {
						wirisButtonIncluded = true;
					}
					else {
						CKEDITOR.config[_wrs_toolbarName].splice(i, 1);
						i--;
					}
				}
			}
		}
		
		var element;
		_wrs_int_directionality = editor.config.contentsLangDirection;
		
		var lastDataSet = null;
		
		editor.on('dataReady', function (e) {
			lastDataSet = editor.getData();
		});
		
		function whenDocReady() {
			if (window.wrs_initParse && lastDataSet != null) {
				var newData = wrs_initParse(lastDataSet);
				
				editor.setData(newData, function (e) {
					var changingMode = false;
					
					editor.on('beforeSetMode', function (e) {
						changingMode = true;
					});
					
					editor.on('mode', function (e) {
						changingMode = false;
					});
					
					editor.on('getData', function (e) {
						if (changingMode) {
							return;
						}
						
						e.data.dataValue = wrs_endParse(e.data.dataValue);
					});
					
					if (editor._.events.doubleclick) {					// When the element is double clicked, a dialog is open. This must be avoided.
						editor._.events.doubleclick.listeners = [];
					}
				});
			}
			else {
				setTimeout(whenDocReady, 50);
			}
		}
		
		whenDocReady();
		
		function checkElement() {
			try {
				var newElement;
				
				if (editor.elementMode == CKEDITOR.ELEMENT_MODE_INLINE) {
					newElement = editor.element.$;
				}
				else {
					var elem = document.getElementById('cke_contents_' + editor.name) ? document.getElementById('cke_contents_' + editor.name) : document.getElementById('cke_' + editor.name);
					newElement = elem.getElementsByTagName('iframe')[0];
				}
				
				if (element != newElement) {
					if (editor.elementMode == CKEDITOR.ELEMENT_MODE_INLINE) {
					    wrs_addElementEvents(newElement, function (div, element) {
					        wrs_int_doubleClickHandlerForDiv(editor, div, element);
					    }, wrs_int_mousedownHandler, wrs_int_mouseupHandler, function (div, element) { timedClick(editor, div, element); });
					}
					else {					    
						wrs_addIframeEvents(newElement, function (iframe, element) {
							wrs_int_doubleClickHandlerForIframe(editor, iframe, element);
						}, wrs_int_mousedownHandler, wrs_int_mouseupHandler, function (iframe, element) { timedClick(editor, iframe, element); });
					}
						
					element = newElement;
				}
			}
			catch (e) {
			}
		}
		
		// CKEditor replaces several times the element element during its execution, so we must assign the events again.
		setInterval(checkElement, 500);
		
		if (_wrs_conf_editorEnabled) {
			_wrs_int_directionality = editor.config.contentsLangDirection;
			
			editor.addCommand('ckeditor_wiris_openFormulaEditor', {
				'async': false,
				'canUndo': false,
				'editorFocus': false,
				'allowedContent': 'img[align,' + _wrs_conf_imageMathmlAttribute + ',src,alt](!Wirisformula)',
				'requiredContent': 'img[align,' + _wrs_conf_imageMathmlAttribute + ',src,alt](Wirisformula)',
				
				'exec': function (editor) {
					wrs_int_openNewFormulaEditor(element, editor.langCode, editor.elementMode != CKEDITOR.ELEMENT_MODE_INLINE);
				}
			});
			
			editor.ui.addButton('ckeditor_wiris_formulaEditor', {
				'label': '&#x516C;&#x5F0F;&#x7F16;&#x8F91;&#x5668;',
				'command': 'ckeditor_wiris_openFormulaEditor',
				'icon': _wrs_int_editorIcon
			});

			_wrs_int_wirisProperties = {};

			if ('wirisimagecolor' in editor.config) {
				_wrs_int_wirisProperties['color'] = editor.config['wirisimagecolor'];
			}			
			
			if ('wirisimagebgcolor' in editor.config) {
				_wrs_int_wirisProperties['bgColor'] = editor.config['wirisimagebgcolor'];
			}

			if ('wirisbackgroundcolor' in editor.config) {
				_wrs_int_wirisProperties['backgroundColor'] = editor.config['wirisbackgroundcolor'];
			}
			
			if ('wirisimagesymbolcolor' in editor.config) {
				_wrs_int_wirisProperties['symbolColor'] = editor.config['wirisimagesymbolcolor'];
			}

			if ('wirisimagenumbercolor' in editor.config) {
				_wrs_int_wirisProperties['numberColor'] = editor.config['wirisimagenumbercolor'];
			}

			if ('wirisimageidentcolor' in editor.config) {
				_wrs_int_wirisProperties['identColor'] = editor.config['wirisimageidentcolor'];
			}
			
			if ('wiristransparency' in editor.config) {
				_wrs_int_wirisProperties['transparency'] = editor.config['wiristransparency'];
			}
			
			if ('wirisimagefontsize' in editor.config) {
				_wrs_int_wirisProperties['fontSize'] = editor.config['wirisimagefontsize'];
			}

			if ('wirisdpi' in editor.config) {
				_wrs_int_wirisProperties['dpi'] = editor.config['wirisdpi'];
			}
		}
		
		if (_wrs_conf_CASEnabled) {
			var allowedContent = 'img[width,height,align,src,' + _wrs_conf_CASMathmlAttribute + '](!Wiriscas); ';
			allowedContent += 'applet[width,height,align,code,archive,codebase,alt,src](!Wiriscas); ';
			allowedContent += 'param[name,value]';
			
			var requiredContent = 'img[width,height,align,src,' + _wrs_conf_CASMathmlAttribute + '](Wiriscas); ';
			requiredContent += 'applet[width,height,align,code,archive,codebase,alt,src](!Wiriscas); ';
			requiredContent += 'param[name,value]';
			
			editor.addCommand('ckeditor_wiris_openCAS', {
				'async': false,								// The command need some time to complete after exec function returns.
				'canUndo': false,
				'editorFocus': false,
				'allowedContent': allowedContent,
				'requiredContent': requiredContent,
				
				'exec': function (editor) {
					wrs_int_openNewCAS(element, editor.elementMode != CKEDITOR.ELEMENT_MODE_INLINE, editor.langCode);
				}
			});
			
			editor.ui.addButton('ckeditor_wiris_CAS', {
				'label': 'WIRIS cas',
				'command': 'ckeditor_wiris_openCAS',
				'icon': _wrs_int_CASIcon
			});			
		}
		var editorId = editor.element.getAttribute('id');
		editor.on('instanceReady', function () {
		    document.getElementById(editorId).removeAttribute("title");
		    document.getElementById(editorId).removeAttribute("aria-label");
		});
	}    
});

/** simulate the double click to edit an existing formula for cefglue browser as it has no impletation for dblclick event -- by ldj **/
var formulaClickCnt = 0;
var timer = null;
var curEditor = null, curDiv = null, curElem = null;
function timedClick(editor, div, element) {
    curEditor = editor;
    curDiv = div;
    curElem = element;
    formulaClickCnt++;
    curElem.style.maxWidth = "100%";
    if (formulaClickCnt == 1) {
        var jscode = "if (formulaClickCnt == 2) {    \
                        editExistingFormula();       \
                      }                              \
                      formulaClickCnt = 0;";
        if (timer != null) clearTimeout(timer);
        timer = setTimeout(jscode, 200);
    }

    if (formulaClickCnt > 2) {
        formulaClickCnt = 0;
    }
}

/**
 * Handles a double click on the contentEditable div.
 * @param object div Target
 * @param object element Element double clicked
 */
function editExistingFormula() {
    if (curEditor.elementMode == CKEDITOR.ELEMENT_MODE_INLINE) {
        wrs_int_doubleClickHandlerForDiv(curEditor, curDiv, curElem);
        //wrs_int_doubleClickHandler(curEditor, curDiv, false, curElem);
    } else {
        wrs_int_doubleClickHandlerForIframe(curEditor, curDiv, curElem);
    }
}
/** simulation end **/

/**
 * Opens formula editor.
 * @param object element Target
 */
function wrs_int_openNewFormulaEditor(element, language, isIframe) {
	if (_wrs_int_window_opened) {
		_wrs_int_window.focus();
	}
	else {
		_wrs_int_window_opened = true;
		_wrs_isNewElement = true;
		_wrs_int_temporalElement = element;
		_wrs_int_temporalElementIsIframe = isIframe;
		_wrs_int_window = wrs_openEditorWindow(language, element, isIframe);
	}
}

/**
 * Opens CAS.
 * @param object element Target
 */
function wrs_int_openNewCAS(element, isIframe, language) {
	if (_wrs_int_window_opened) {
		_wrs_int_window.focus();
	}
	else {
		_wrs_int_window_opened = true;
		_wrs_isNewElement = true;
		_wrs_int_temporalElement = element;
		_wrs_int_temporalElementIsIframe = isIframe;
		_wrs_int_window = wrs_openCASWindow(element, isIframe, language);
	}
}

/**
 * Handles a double click on the contentEditable div.
 * @param object div Target
 * @param object element Element double clicked
 */
function wrs_int_doubleClickHandlerForDiv(editor, div, element) {
	wrs_int_doubleClickHandler(editor, div, false, element);
}

/**
 * Handles a double click on the iframe.
 * @param object div Target
 * @param object element Element double clicked
 */
function wrs_int_doubleClickHandlerForIframe(editor, iframe, element) {
	wrs_int_doubleClickHandler(editor, iframe, true, element);
}

/**
 * Handles a double click.
 * @param object target Target
 * @param object element Element double clicked
 */
function wrs_int_doubleClickHandler(editor, target, isIframe, element) {
	if (element.nodeName.toLowerCase() == 'img') {
		if (wrs_containsClass(element, _wrs_conf_imageClassName)) {
			if (!_wrs_int_window_opened) {
				_wrs_temporalImage = element;
				wrs_int_openExistingFormulaEditor(target, isIframe, editor.langCode);
			}
			else {
				_wrs_int_window.focus();
			}
		}
		else if (wrs_containsClass(element, _wrs_conf_CASClassName)) {
			if (!_wrs_int_window_opened) {
				_wrs_temporalImage = element;
				wrs_int_openExistingCAS(target, isIframe, editor.langCode);
			}
			else {
				_wrs_int_window.focus();
			}
		}
	}
}

/**
 * Opens formula editor to edit an existing formula.
 * @param object element Target
 * @param boolean isIframe
 */
function wrs_int_openExistingFormulaEditor(element, isIframe, language) {
	_wrs_int_window_opened = true;
	_wrs_isNewElement = false;
	_wrs_int_temporalElement = element;
	_wrs_int_temporalElementIsIframe = isIframe;
	_wrs_int_window = wrs_openEditorWindow(language, element, isIframe);
}

/**
 * Opens CAS to edit an existing formula.
 * @param object iframe Target
 */
function wrs_int_openExistingCAS(element, isIframe, language) {
	_wrs_int_window_opened = true;
	_wrs_isNewElement = false;
	_wrs_int_temporalElement = element;
	_wrs_int_temporalElementIsIframe = isIframe;
	_wrs_int_window = wrs_openCASWindow(element, isIframe, language);
}

/**
 * Handles a mouse down event on the iframe.
 * @param object iframe Target
 * @param object element Element mouse downed
 */
function wrs_int_mousedownHandler(iframe, element) {
	if (element.nodeName.toLowerCase() == 'img') {
		if (wrs_containsClass(element, 'Wirisformula') || wrs_containsClass(element, 'Wiriscas')) {
			// delete next line to avoid a long formula image to be zoomed in by user click on it -- by ldj 2016.11.03
			//_wrs_int_temporalImageResizing = element;
		}
	}
}

/**
 * Handles a mouse up event on the iframe.
 */
function wrs_int_mouseupHandler() {
	if (_wrs_int_temporalImageResizing) {
		setTimeout(function () {
			_wrs_int_temporalImageResizing.removeAttribute('style');
			_wrs_int_temporalImageResizing.removeAttribute('width');
			_wrs_int_temporalImageResizing.removeAttribute('height');
		}, 10);
	}
}

/**
 * Calls wrs_updateFormula with well params.
 * @param string mathml
 */
function wrs_int_updateFormula(mathml, editMode, language) {
	if (_wrs_int_temporalElementIsIframe) {
		wrs_updateFormula(_wrs_int_temporalElement.contentWindow, _wrs_int_temporalElement.contentWindow, mathml, _wrs_int_wirisProperties, editMode, language);
	}
	else {
		wrs_updateFormula(_wrs_int_temporalElement, window, mathml, _wrs_int_wirisProperties, editMode, language);
	}
}

/**
 * Calls wrs_updateCAS with well params.
 * @param string appletCode
 * @param string image
 * @param int width
 * @param int height
 */
function wrs_int_updateCAS(appletCode, image, width, height) {
	if (_wrs_int_temporalElementIsIframe) {
		wrs_updateCAS(_wrs_int_temporalElement.contentWindow, _wrs_int_temporalElement.contentWindow, appletCode, image, width, height);
	}
	else {
		wrs_updateCAS(_wrs_int_temporalElement, window, appletCode, image, width, height);
	}
}

/**
 * Handles window closing.
 */
function wrs_int_notifyWindowClosed() {
	_wrs_int_window_opened = false;
}
