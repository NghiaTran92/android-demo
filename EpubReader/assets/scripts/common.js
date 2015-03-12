$(function() {
    var width = 0;
    if (typeof(document.body.clientWidth) == 'number') {
        // newest gen browsers
        width += document.body.clientWidth;
    }
    initCalculerPage(width);
});
var pageCount = 0;
var widthColumn=0;
var fullW=0;



//function scrollFunction() {
//	var posX=this.scrollX;
//	var posY=this.scrollY;
//	var mes="posX:"+posX+"-- poxY:"+posY;
//	
//	//window.android.logWebview(mes);
//}
//
//window.onscroll = scrollFunction;

// position is page-1 number
function scrollTo (position) {
    var width = 0;
    if (typeof(document.body.clientWidth) == 'number') {
	    // newest gen browsers
	    width += document.body.clientWidth;
	}
    var scroll = position * width;
     $('html, body').stop().animate({
                        scrollLeft: scroll
                    }, 300);
}

function setPageNumber(page){
	if(page>=1 && page<=pageCount){
		currPosition=(page-1)*widthColumn;
		scrollTo(page-1);
		window.android.onPagedChanged(page);
	}
}

//position is page -1 number
function scrollNoAnimation(position) {
    var width = 0;
    if (typeof(document.body.clientWidth) == 'number') {
        // newest gen browsers
        width += document.body.clientWidth;
    }
    var scroll = position * width;
     $('html, body').stop().animate({
                        scrollLeft: scroll
                    }, 0);
}

var currPosition=0;
var isSetCurrPos=true;

function scrollNear(deltaX){
	if(isSetCurrPos){
		currPosition=window.scrollX;
		isSetCurrPos=false;
	}
	var moveto=currPosition+deltaX;
	window.android.logWebview("current pos x:"+moveto);
	  $('html, body').stop().animate({
          scrollLeft: moveto
      }, 0);
}

function activeSetCurrPos(){
	isSetCurrPos=true;
}

function reloadExactlyPage(){
	var currPage=Math.floor(window.scrollX/widthColumn)+1;
	window.android.logWebview("-------current page:"+currPage+"-- scrollX:"+window.scrollX+"--widthColumn:"+widthColumn);
	var start=(currPage-1)*widthColumn;
	var end=start+widthColumn;
	var center=Math.ceil((start+end)/2);
	window.android.logWebview("center:"+center);
	if(window.scrollX<center){
		window.android.logWebview("smaller");
		window.android.onPagedChanged(currPage);
		scrollTo(currPage-1);
		currPosition=(currPage-1)*widthColumn;
	}else if(currPage+1<=pageCount){
		window.android.logWebview("bigger");
		window.android.onPagedChanged(currPage+1);
		scrollTo(currPage);
		currPosition=(currPage-1)*widthColumn;
	}
}

function initCalculerPage(ourW) {
    var d = document.body;
    widthColumn=ourW;
    fullW = d.scrollWidth;
    pageCount = Math.ceil(fullW / ourW);
    var mes="width:"+ourW;
    window.android.logWebview(mes);
    window.android.setPageCount(pageCount);
    window.android.onPaged(pageCount);
}

    function changeFont(){
        
        var font_size = "11px";
        var body_tag = document.getElementsByTagName('body');
        for (var i = body_tag.length - 1; i >= 0; i--) {
            body_tag[i].style.fontSize = font_size;
        };

        var div_tags = document.getElementsByTagName('div');
        for (var i = div_tags.length - 1; i >= 0; i--) {
            div_tags[i].style["font-size"] = font_size;
        };

        var p_tags = document.getElementsByTagName('p');
        for (var i = p_tags.length - 1; i >= 0; i--) {
            p_tags[i].style["font-size"] = font_size;
        };

        var span_tags = document.getElementsByTagName('span');
        for (var i = span_tags.length - 1; i >= 0; i--) {
            span_tags[i].style["font-size"] = font_size;
        };


    }

    function test(){
        var tx = window.android.getFontSize();
        alert(tx);
    }