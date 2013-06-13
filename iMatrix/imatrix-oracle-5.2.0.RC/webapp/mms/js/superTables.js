
function initTableScroll(divId){
	//start = new Date();
	$('#'+divId).css('display', 'block');
	var sHeaderInner = $($($('#'+divId).children('.sBase')).children('.sHeader')).children('.sHeaderInner');
	var ths = $($($($(sHeaderInner).children('table')).children('thead')).children('tr')).children('th');
	var cg = getColgroups(ths);
	$($(sHeaderInner).children('table')).prepend(cg);
	var sData = $($('#'+divId).children('.sBase')).children('.sData');
	$(sData).css('height', ($($('#'+divId).children('.sBase')).height()-28)+"px");
	$(sData).css('width', ($($('#'+divId).children('.sBase')).width())+"px");
	$($(sData).children('table')).prepend(cg);
	$(sData).scroll(function(){
		$(sHeaderInner).css('right', $(sData).scrollLeft()+"px");
	});
	if (/*@cc_on!@*/0) { /* Internet Explorer */
		window.attachEvent("onunload", function () {
			sData.onscroll = null;
		});
	}
	//alert(((new Date()) - this.start));
}
function getColgroups(ths){
	var cg = "<colgroup>";
	for(var i = 0; i < ths.length; i++){
		cg += "<col width='"+($(ths[i]).width()+1)+"'>";
	}
	cg += "</colgroup>";
	return cg;
}
function initScrollSize(){
	var col3 = $("#col3").height();
	var width = $(window).width()-235;
	var height = col3-120;
	$('.fakeContainer').css('width', width);
	$('.fakeContainer').css('height', height);
	initTableScroll('page0');
}

