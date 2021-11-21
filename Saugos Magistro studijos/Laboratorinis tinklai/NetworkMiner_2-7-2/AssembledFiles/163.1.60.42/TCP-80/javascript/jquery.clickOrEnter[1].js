/*
@author Bryan Gullan
@description Bind mouse click and enter key to a given element. On Click or Enter, specified function is called and default event action blocked.

Sample use is to add popup to a link, where the href would be followed if javascript were turned off.


var popup = function() {
	alert('activated');	
};
$(document).ready(function() {
	$.clickOrEnter('a',popup);
});


var popupKnowing = function(thing) {
	thing.style.display = '';
	alert('shown');
}
$(document).ready(function() {
	$.clickOrEnterWithElem('a',popupKnowing);
});


(c) 2007 Bryan Gullan.
Use and distribute freely with this header intact
http://jquery.com/plugins/project/clickOrEnter

*/

jQuery.clickOrEnter = function(element,callback) {
	jQuery(element).bind('click', function(event) {
  		callback();
  		event.preventDefault(); //prevent browser from following the actual href
	});
	jQuery(element).bind('keypress', function(event) {
		var code=event.charCode || event.keyCode;
		if(code && code == 13) {// if enter is pressed
  			callback();
  			event.preventDefault(); //prevent browser from following the actual href
		};
	});
};

jQuery.clickOrEnterWithElem = function(element,callback) {
	jQuery(element).bind('click', function(event) {
  		callback(event.target);
  		event.preventDefault(); //prevent browser from following the actual href
	});
	jQuery(element).bind('keypress', function(event) {
		var code=event.charCode || event.keyCode;
		if(code && code == 13) {// if enter is pressed
  			callback(event.target);
  			event.preventDefault(); //prevent browser from following the actual href
		};
	});
};
