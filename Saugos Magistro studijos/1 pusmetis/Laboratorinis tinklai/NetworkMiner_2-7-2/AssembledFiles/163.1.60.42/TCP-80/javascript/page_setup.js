/*
@description Add general site-wide js funtionality to the page (e.g. print page button)

@author	Bryan Gullan
@created 2007-08-28

Notes: Uses jQuery; written on v. 1.1.4
			Requires jQuery plugin clickOrEnter: http://jquery.com/plugins/project/clickOrEnter
*/

var newWindow = null;

$(document).ready(function() {
	// deal with search box
	var defaultText = "Enter search term";
	$('#searchinput').val(defaultText).focus(function() {
		if (this.value == defaultText) {
			$(this).val('');
		}
	}).blur(function() {
		if (this.value == '') {
			$('#searchinput').val(defaultText);
		}
	});
	
	// add print page link - since it can only work for JS, only add it for JS users
	$('<li></li>') // create list item
	.addClass('page-print') //add relevant class to it
	.append('<a href="javascript:window.print();">Print this page</a>') //put in the link
	.insertBefore('.page-recommend'); //add before the recommend this page item
	
	//attach js funtionality to recommend page
	$.clickOrEnter('li.page-recommend a',email_to_friend);
	
	//attach js funtionality to post a comment
	$.clickOrEnter('.post-comment a',post_comment);
	//attach js funtionality to video link
	$.clickOrEnterWithElem('a.launch-video',videoconsole);
	
	$.clickOrEnter('a.sitemap',showSiteMap);
	
	//IE6 hack for nav drop-downs
	$('ul#nav > li').bind('mouseover', function(event) {
		$(this).addClass('over');
	});
	$('ul#nav > li').bind('mouseout', function(event) {
		$(this).removeClass('over');
	});
	//IE6 hack for world map
	$('ul.world-map li').bind('mouseover', function(event) {
		$(this).addClass('over');
	});
	$('ul.world-map li').bind('mouseout', function(event) {
		$(this).removeClass('over');
	});
	
	//deal with brower inconsistencies
	if($.browser.name=="chrome"){
		$('head').append('<link rel="stylesheet" href="/css/front/chrome.css" type="text/css" />');
	}
	if($.browser.name=="netscape" && $.browser.versionNumber<=8){
		$("head").append('<link rel="stylesheet" href="/css/front/ns.css" type="text/css" />');
	}
	
	/* setup popups */
	$.clickOrEnterWithElem('a[rel~=popup]',doPopup);
	$('a[rel~=popup]').addClass("popup");
	
	/* open rel="external" links in a new window */
	$.clickOrEnterWithElem('a[rel~=external]',openInNewWindow);
	$('a[rel~=external]').addClass("external").after("<br />(opens in a new window)");
	
});


// SEND TO FRIEND

function closeRecommend() {
	$('#recommend-box').fadeOut(1200,function(){
		$('#recommend-box').remove();
	});
}

function setupRecommendBox() {
	var recommendLocation = $('.page-recommend').offset();
	if($.browser.msie && $.browser.version <= 6) {
		var docHeight = $(document).height();
	}
	else {
		var docHeight = $(window).height();
	}
	
	var boxLeft = recommendLocation.left - 230 + $('.page-recommend').width();
	
	var boxBottom = docHeight - recommendLocation.top - ( 2* $('.page-recommend').height());
	$('#recommend-box').css({left: boxLeft + 'px', bottom: boxBottom + 'px'});
		var senderText = "Your name";
		var SenderEmailText = "Your email";
		var recipientText = "Their name";
		var recipientEmailText = "Their email";
		
		$('#recommend-box #senderName').val(senderText).focus(function() {
			if (this.value == senderText) {
				$(this).val('');
			}
		}).blur(function() {
			if (this.value == '') {
				$('#recommend-box #senderName').val(senderText);
			}
		});
		$('#recommend-box #sender').val(SenderEmailText).focus(function() {
			if (this.value == SenderEmailText) {
				$(this).val('');
			}
		}).blur(function() {
			if (this.value == '') {
				$('#recommend-box #sender').val(SenderEmailText);
			}
		});
		
		$('#recommend-box #recipientName').val(recipientText).focus(function() {
			if (this.value == recipientText) {
				$(this).val('');
			}
		}).blur(function() {
			if (this.value == '') {
				$('#recommend-box #recipientName').val(recipientText);
			}
		});
		$('#recommend-box #recipient').val(recipientEmailText).focus(function() {
			if (this.value == recipientEmailText) {
				$(this).val('');
			}
		}).blur(function() {
			if (this.value == '') {
				$('#recommend-box #recipient').val(recipientEmailText);
			}
		});
		var boxTimeOut;
		$('#recommend-box form').submit(function() {
			$.post('/applications/send_to_friend/js_send.rm', $(this).find('input').serialize(), function(data) {
				$('#recommend-box div.container').html(data);
				if($('#recommend-box p.thanks').length ) {
					boxTimeOut = setTimeout(closeRecommend,500);
				}
				else {
					setupRecommendBox();
				}
			});
			
			return false;
		});
		
		if($('#recommend-box a.closerlink').length ) {
			$('#recommend-box a.closerlink').remove();			
		}
		if($('#recommend-box img.boxlabel').length ) {
			$('#recommend-box img.boxlabel').remove();			
		}
		var closer = $('<a href="#" class="closerlink"><img alt="close" class="overlayclose fauxlink" src="/display_images/icons/recommend_close.gif" /></a>');
		var boxlabel = $('<img src="/display_images/icons/footer_recommend_popup.gif" alt="recommend this page" class="boxlabel" />');
		$.clickOrEnter(closer,closeRecommend);
		$('#recommend-box').append(closer);
		$('#recommend-box').append(boxlabel);
		/*$('#recommend-box').blur(function() {
			closeRecommend();
		});*/
	$('#recommend-box').show();
}

function email_to_friend() {
	var pageID = $('li.page-recommend a').attr('href').replace(/\/applications\/send_to_friend\/compose.rm\?id=/,""); //get the ID of the page to be recommended
	
	$('<div id="recommend-box"></div>')
	.insertBefore('#header-wrapper');
	$('#recommend-box').hide().load('/applications/send_to_friend/form.rm?id=' +pageID,setupRecommendBox);

	//window.open('/applications/send_to_friend/compose.rm?id='+pageID,'Email','width=380,height=550,scrollbars=1');
}

function post_comment() {
	var pageID = $('li.post-comment a').attr('href').replace(/\/applications\/comments\/comments.rm\?article_id=/,""); //get the ID of the page to be commented on
	window.open('/applications/comments/comments.rm?article_id='+pageID,'Comment','width=480,height=590,scrollbars=1');
}

function videoconsole(element) {
	var ids = $(element).attr('href').replace(/\/applications\/mediaplayer\/video.rm\?media_id=/,""); //get the media and page ids
	ids = ids.split("&id="); // split the two ids; they're either side of the '&id='
	window.open('/applications/mediaplayer/video.rm?media_id='+ids[0]+'&id='+ids[1],'Video console','width=450,height=450,scrollbars=1');
}

function closeSitemap() {
	$('#sitemap').remove();
	$('#overlay').remove();
	$('select').show();
	$('img.overlayclose').unbind('click').unbind('keypress');
	$('#overlay').unbind('click').unbind('keypress');
}

function setupSitemap() {
	var sitemapHeight = $('#sitemap-box').height();
	var docHeight = $(document).height();
	
	if (sitemapHeight + 40 > docHeight) {
		$('body').height(sitemapHeight + 60 + 'px');
		docHeight = $(document).height();
		$('#overlay').css({ height: docHeight + 'px' });
	}
	var sitemapTopMargin = docHeight - sitemapHeight - 40;
	//$('#sitemap-box').css({marginTop: sitemapTopMargin + 'px' });  
	$('#sitemap-box').css({marginTop: 40 + 'px' });  
	
	var closer = $('<a href="#"><img alt="close sitemap" class="overlayclose fauxlink" src="/display_images/sitemap_close.gif" /></a>')
	$.clickOrEnter(closer,closeSitemap);
	$('#sitemap-box').append(closer);
	$.clickOrEnter('#overlay',closeSitemap);
	$('select').hide();
	$('#sitemap-box').show();
	//call google analytics tracking for dummy page for this popup
	if (typeof urchinTracker == 'function') {
		urchinTracker('/sitemap/');
	} 
}

function showSiteMap() {
	var docHeight = $(document).height();
	var docWidth = $(document).width();
	
	var sitemapType = $('a.sitemap').attr('title'); //the title attribute is set to be the sitemap type
	
	$('<div id="overlay"></div>').insertAfter('#wrapper');
	$('<div id="sitemap"></div>').insertAfter('#wrapper');
	
	window.scrollTo(0,0);
	
	$('#overlay').css({ height: docHeight + 'px' ,width: docWidth + 'px'  });
	$('#sitemap').load('/applications/sitemap.rm?site=' + sitemapType,setupSitemap)
	;
}

function doPopup(element){
	var wintype = "standard";
	var winwidth = "780";
	var winheight = "580";

	var attribs = $(element).attr("rel").split(" ");
	if (attribs[1]!=null) {wintype = attribs[1].toLowerCase();}
	if (attribs[2]!=null) {winwidth = attribs[2];}
	if (attribs[3]!=null) {winheight = attribs[3];}
	
	if (newWindow != null && !newWindow.closed){
		newWindow.close();
	}
	
	if (wintype == "fullscreen"){
		winwidth = screen.availWidth;
		winheight = screen.availHeight;
	}
	var tools="";
	if (wintype == "standard") tools = "resizable,toolbar=yes,location=yes,scrollbars=yes,menubar=yes,width="+winwidth+",height="+winheight+",top=0,left=0";
	if (wintype == "console" || wintype == "fullscreen") tools = "resizable,toolbar=no,location=no,scrollbars=no,width="+winwidth+",height="+winheight+",left=0,top=0";
	newWindow = window.open($(element).attr("href"), 'newWin', tools);
	newWindow.focus();

	return false;
}

function openInNewWindow(element) {
	window.open(element.href);
}
