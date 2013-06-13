$(function(){
		var iframe=$('iframe')[0];
		var isIE = $.browser.msie && !$.support.opacity;
		if (isIE) {
			iframe.allowTransparency = "true";
			iframe.frameBorder=0;
		}
	}
)

