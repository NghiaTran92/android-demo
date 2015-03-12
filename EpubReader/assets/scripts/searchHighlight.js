var foundStartNode = false;
var foundEndNode = false;
var range;
var yPos = 0;
var pageCount = 0;
var bodyOffsetHeight = 0;

var searchKeyword = '';
var vegaMarkKeyword = "-vega-highlight";
var isHighlighted = false;
var timeOut = 200;

String.prototype.latinise=function(){
	var defaultDiacriticsRemovalMap = [
		{'base':'a', 'letters':/[\u0061\u24D0\uFF41\u1E9A\u00E0\u00E1\u00E2\u1EA7\u1EA5\u1EAB\u1EA9\u00E3\u0101\u0103\u1EB1\u1EAF\u1EB5\u1EB3\u0227\u01E1\u00E4\u01DF\u1EA3\u00E5\u01FB\u01CE\u0201\u0203\u1EA1\u1EAD\u1EB7\u1E01\u0105\u2C65\u0250]/g},
		{'base':'d', 'letters':/[\u0064\u24D3\uFF44\u1E0B\u010F\u1E0D\u1E11\u1E13\u1E0F\u0111\u018C\u0256\u0257\uA77A]/g},
		{'base':'e', 'letters':/[\u0065\u24D4\uFF45\u00E8\u00E9\u00EA\u1EC1\u1EBF\u1EC5\u1EC3\u1EBD\u0113\u1E15\u1E17\u0115\u0117\u00EB\u1EBB\u011B\u0205\u0207\u1EB9\u1EC7\u0229\u1E1D\u0119\u1E19\u1E1B\u0247\u025B\u01DD]/g},
		{'base':'i', 'letters':/[\u0069\u24D8\uFF49\u00EC\u00ED\u00EE\u0129\u012B\u012D\u00EF\u1E2F\u1EC9\u01D0\u0209\u020B\u1ECB\u012F\u1E2D\u0268\u0131]/g},
		{'base':'o', 'letters':/[\u006F\u24DE\uFF4F\u00F2\u00F3\u00F4\u1ED3\u1ED1\u1ED7\u1ED5\u00F5\u1E4D\u022D\u1E4F\u014D\u1E51\u1E53\u014F\u022F\u0231\u00F6\u022B\u1ECF\u0151\u01D2\u020D\u020F\u01A1\u1EDD\u1EDB\u1EE1\u1EDF\u1EE3\u1ECD\u1ED9\u01EB\u01ED\u00F8\u01FF\u0254\uA74B\uA74D\u0275]/g},
		{'base':'u','letters':/[\u0075\u24E4\uFF55\u00F9\u00FA\u00FB\u0169\u1E79\u016B\u1E7B\u016D\u00FC\u01DC\u01D8\u01D6\u01DA\u1EE7\u016F\u0171\u01D4\u0215\u0217\u01B0\u1EEB\u1EE9\u1EEF\u1EED\u1EF1\u1EE5\u1E73\u0173\u1E77\u1E75\u0289]/g},
		{'base':'y','letters':/[\u0079\u24E8\uFF59\u1EF3\u00FD\u0177\u1EF9\u0233\u1E8F\u00FF\u1EF7\u1E99\u1EF5\u01B4\u024F\u1EFF]/g}
	];
	var result = this.toLowerCase();
    for(var i = 0; i < defaultDiacriticsRemovalMap.length; i++) {
        result = result.replace(defaultDiacriticsRemovalMap[i].letters, defaultDiacriticsRemovalMap[i].base);
    }
    return result;
};

function MyApp_MarkHightlightKeyword(element, keyword, isSameContainer) {
    if (element) {
        if (element.nodeType == 3) {// Text node
            if(element.parentNode.getAttribute("class") == "MyAppHighlight") {
                isHighlighted = true;
                return;
            }
            var value = element.nodeValue;  // Search for keyword in text node
            var idx = value.indexOf(keyword); 
            var text;
            var span;
            var textNode;
            var startOffset;
            
            if(foundStartNode && !isSameContainer) {
                startOffset = 0;
            } else {
                startOffset = range.startOffset;
            }
            if(value.substr(startOffset, keyword.length) == keyword) {
                if (!foundStartNode || !foundEndNode) {
                    if (idx >= 0) {
                        text = value.substr(idx,keyword.length);
                        textNode = document.createTextNode(text + vegaMarkKeyword);
                        span = document.createElement("highlight");
                        span.className = "MyAppHighlight";
                        span.appendChild(textNode);
                        
                        element.deleteData(idx, value.length - idx);
                        
                        var rightText = document.createTextNode(value.substr(idx + keyword.length));
                        var next = element.nextSibling;
                        
                        if (rightText.nodeValue === "") {
                            if (next === null) {
                                element.parentNode.appendChild(span);
                            } else{
                                element.parentNode.insertBefore(span, next);
                            }
                        } else {
                            if (next === null) {
                                element.parentNode.appendChild(rightText);
                                element.parentNode.insertBefore(span, rightText);
                            } else{
                                element.parentNode.insertBefore(rightText, next);
                                element.parentNode.insertBefore(span, rightText);
                            }
                        }
                        span.innerHTML = text;
                        if (!foundEndNode && foundStartNode) {
                            foundEndNode = true;
                            if(!isSameContainer) {
                                element.parentNode.removeChild(element);
                            }
                        }
                        if (!foundStartNode) {
                            foundStartNode = true;
							yPos = getPos(span).y;
                        }
                    }
                }
            } 
            else if (foundStartNode && !foundEndNode) {
                text = document.createTextNode(value);
                span = document.createElement("highlight");
                span.className = "MyAppHighlight";
                span.appendChild(text);
                element.parentNode.replaceChild(span, element);
            }
        } else if (element.nodeType == 1) { // Element node
            if (element.style.display != "none" && element.nodeName.toLowerCase() != 'select') {
                var n = element.childNodes.length;
                for (var i = 0; i < n; i++) {
                    MyApp_MarkHightlightKeyword(element.childNodes[i],keyword);
                }
            }
        }
    }
}

function MyApp_HighlightAllOccurencesOfString(keyword, padding) {
	keyword = (keyword + "").latinise();
	var endBody = document.getElementById('vef_end_body');
	if(typeof endBody == 'undefined'){
		window.android.onHighlighted(1);
		return;
	}
	
	var delta = 0;
	if(searchKeyword == keyword){
		var pageNumber = Math.max(Math.ceil(Number(yPos / endBody.offsetTop * pageCount).toFixed(3)), 1);
		window.android.onHighlighted(pageNumber);
		return;
	}
	
	if(searchKeyword != ''){
		MyApp_RemoveAllHighlights();
	}
	
	searchKeyword = keyword;
	yPos = 0;
	
    if(window.find(keyword) || window.find(keyword, false, true)) {
        foundStartNode = false;
		foundEndNode = false;
		isHighlighted = false;
		
		var sel = window.getSelection();
		if(sel.rangeCount > 0){
			if (sel.getRangeAt) {
				range = sel.getRangeAt(0);
			} else{
				range = document.createRange();
				range.setStart(sel.anchorNode,sel.anchorOffset);
				range.setEnd(sel.focusNode,sel.focusOffset);
			}
			
			sel = window.getSelection();
			if(sel.rangeCount > 0)  {
				sel.removeAllRanges();
			} 
			sel.addRange(range);
			
			var startContainer = range.startContainer;
			var endContainer = range.endContainer;
			if(startContainer.nodeName != "BODY" && endContainer.nodeName != "BODY"){
				var startKeyword = startContainer.textContent.substr( range.startOffset );
				var endKeyword = endContainer.textContent.substr( 0, range.endOffset );
				var ancestorContainer = range.commonAncestorContainer;
				var childNodes = ancestorContainer.childNodes;
				var NumberOfChildNodes = childNodes.length;
				var i = 0;
				
				if (startContainer.textContent == endContainer.textContent) {
					childNodes = startContainer.parentNode.childNodes;
					NumberOfChildNodes = childNodes.length;
					for (i = 0; i < NumberOfChildNodes; i++) {
						if (childNodes[i].textContent == startContainer.textContent) {
							startKeyword = childNodes[i].textContent.substr(range.startOffset, range.endOffset - range.startOffset);
							MyApp_MarkHightlightKeyword(childNodes[i], startKeyword, true);
						}
					}
				} else{
					for (i = 0; i < NumberOfChildNodes; i++) {
						if (!foundStartNode) {
							MyApp_MarkHightlightKeyword( childNodes[i], startKeyword, false );
							if(isHighlighted) {
								break;
							}
						} else if (!foundEndNode){
							MyApp_MarkHightlightKeyword( childNodes[i], endKeyword, false );
						} else {
							break;
						}
					}
				}
			}
		}
    }

	var pageNumber = Math.max(Math.ceil(Number(yPos / endBody.offsetTop * pageCount).toFixed(3)), 1);
	setTimeout(function(){window.android.onHighlighted(pageNumber);}, timeOut);
}

function getPos(el) {
    var lx = ly = 0;
    if(el.offsetParent)
    {
        do {
            lx += el.offsetLeft;
            ly += el.offsetTop;
            el = el.offsetParent;
        } while(el);
    }
    return {x: lx,y: ly};
}

function MyApp_RemoveAllHighlightsForElement(element) {
    if (element) {
        if (element.nodeType == 1) {
            if (element.getAttribute("class") == "MyAppHighlight") {
                var text = element.removeChild(element.firstChild);
                element.parentNode.insertBefore(text,element);
                element.parentNode.removeChild(element);
                return true;
            } else {
                var normalize = false;
                for (var i=element.childNodes.length-1; i>=0; i--) {
                    if (MyApp_RemoveAllHighlightsForElement(element.childNodes[i])) {
                        normalize = true;
                    }
                }
                if (normalize) {
					try{
						element.normalize();
					}catch(e){}
                }
            }
        }
    }
    return false;
}

function MyApp_RemoveAllHighlights() {
    MyApp_RemoveAllHighlightsForElement(document.body);
}