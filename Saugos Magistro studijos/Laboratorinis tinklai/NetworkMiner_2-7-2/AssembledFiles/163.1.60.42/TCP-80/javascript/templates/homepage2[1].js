/*
@description Homepage v2 JS functions
News ticker - taken from jQuery plugin by Bryan Gullan http://www.makemineatriple.com/jQuery

*/

$(document).ready(function() {
	if($('div#newsflash.enabletick').length !=0) {
		newsAnchor = $('div#newsflash a');
		newsLink = $(newsAnchor).attr('href');
		newsText = $('div#newsflash a').text();
		$(newsAnchor).empty();
		tickerTimeOut = 80;
		currentLength = 0;
		setTimeout(runTicker,100);
	}
});
function runTicker() {
	if( currentLength % 2 == 0) {
			placeHolder = "  |";
	}
	else {
		placeHolder = "_";
	}
	if( currentLength <= newsText.length + 1) {
		var tickerText = newsText.substring(0,currentLength);
		newsAnchor.text(tickerText + placeHolder);
		currentLength ++;
		setTimeout(runTicker,tickerTimeOut);
	}
	else {
		newsAnchor.text(newsText);
		currentLength = 0;
		setTimeout(runTicker,4000);	
	}	
}

var title_admissions = new Image();
title_admissions.src = "/display_images/nav/home/admissions.gif";
var title_divisions = new Image();
title_divisions.src = "/display_images/nav/home/divisions.gif";
var title_colleges = new Image();
title_colleges.src = "/display_images/nav/home/colleges.gif";
var title_visitorsandfriends = new Image();
title_visitorsandfriends.src = "/display_images/nav/home/visitorsandfriends.gif";
var title_international = new Image();
title_international.src = "/display_images/nav/home/international.gif";
var title_research = new Image();
title_research.src = "/display_images/nav/home/research.gif";
var title_enterprise = new Image();
title_enterprise.src = "/display_images/nav/home/enterprise.gif";
var title_abouttheuniversity = new Image();
title_abouttheuniversity.src = "/display_images/nav/home/abouttheuniversity.gif";

//include optional Google Chrome stylesheet if browser is chrome
if($.browser.name=="chrome"){
	$('head').append('<link rel="stylesheet" href="/css/front/chrome.css" type="text/css" />');
}

badBrowser=0;
if($.browser.name=="netscape" && $.browser.versionNumber<=8){
	badBrowser=1;
}
if(!badBrowser){
	$(function() {
		$('#features_menu li a').click(function() {$('#features_panel :animated').stop(); showFeature(this.id); return false;});
		$('#features_menu li a').removeClass('current');
		scaleMenuButtons();
		$('#features_menu').css('top', '224px').animate({'top': '196px'}, {duration: 750, complete: featuresMenuShown} );
		/*var buttonwidth = ($('#features_panel').width() / 6);
		$('#features_menu li').css('width', ''+ buttonwidth +'px');*/
		$('#features_submenu').hide();
		$('#features_panel').removeClass('no-js');
		$(window).resize(scaleMenuButtons);
	});
}

function scaleMenuButtons() {
	var menuWidth = $('#features_panel').width();
	var menuItemWidth = Math.round(menuWidth / 6);
	$('#features_menu li').css('width', ''+menuItemWidth+'px');
}

function featuresMenuShown() {
	showFeature('feature_link_1');
}

function showFeature(id) {
	var featureData = featuresData[id];
	$('#features_menu li a').removeClass('current');
	$('#'+id).addClass('current');
	$('#features_panel').attr("class","");
	$('#features_panel').addClass(id+'_selected');
	$('#features_panel').css('background', 'url(' + featureData.image + ') 50% 0 no-repeat');
	$('#features_panel .photo_info').remove();	
	if(featureData.photo_info_link.length){
		if(featureData.photo_info_linktext.length){
			linktext=featureData.photo_info_linktext;
		}else{
			linktext="Photograph information";
		}
		$('#features_panel').prepend('<div class="photo_info"><a href="'+featureData.photo_info_link+'">'+linktext+'</a></div>');
	}
	
	featureData.behaviour(featureData);
	
	//call google analytics tracking for dummy page for this popup
    if (typeof urchinTracker == 'function') {
	    urchinTracker('/homepage_wow/' + id);
    }

}

function showNewsFeature(featureData) {
	
	$('#features_submenu').stop();
	$('#features_submenu').css('top', '196px');
	if (featureData.video) {
		var storiesBar = $('<ul class="feature_stories"><li class="video_panel video'+ featureData.itemclass +'"></li><li class="'+ featureData.itemclass +'"></li><li></li><li></li></ul>');
		var panel = $('<div><div id="feature_video"><div id="flvplayer"></div></div></div>');
		if (featureData.video.height > 120) { /* two valid sizes are 185x139 (4:3) and 185x104 (16:9) */
			/* FIXME: show video_image as static image, if available */
			$('#feature_video', panel).addClass('feature_video_ratio_4_3').hide();
		} else {
			$('#feature_video', panel).addClass('feature_video_ratio_16_9').hide();
		}
		$('li:eq(0)', storiesBar).append(panel);
		for (var i = 0; i < 3; i++) {
			insertNewsStory($('li:eq('+(i+1)+')', storiesBar), featureData.stories[i]);
		}
	} else if (featureData.panel_image) {
		var storiesBar = $('<ul class="feature_stories"><li class="video_panel video'+ featureData.itemclass +'"></li><li class="'+ featureData.itemclass +'"></li><li></li><li></li><li></li></ul>');
		$('li:eq(0)', storiesBar).append(panel);
		for (var i = 0; i < 4; i++) {
			insertNewsStory($('li:eq('+(i+1)+')', storiesBar), featureData.stories[i]);
		}
	} else {
		var storiesBar = $('<ul class="feature_stories"><li class="'+ featureData.itemclass +'"></li><li></li><li></li><li></li></ul>');
		for (var i = 0; i < 4; i++) {
			insertNewsStory($('li:eq('+i+')', storiesBar), featureData.stories[i]);
		}
	}
	$('#features_submenu').empty();
	$('#features_submenu').append(storiesBar);
	if(featureData.itemclass == "feature4" || featureData.itemclass == "feature5"|| featureData.itemclass == "feature7") {
		$('ul.feature_stories').css('float', 'right');
		var videoAlign = 'right';
	}
	else {
		var videoAlign = 'left';
	}
	$('#features_submenu').animate({'top':'196px'}, {duration: 500, complete: function() {
		$('#features_submenu').show().animate({'top': '0px'}, {duration: 500, complete: function() {
			revealVideo(featureData.video,featureData.panel_image,videoAlign);
		}});	
	}});
	

	
}
function revealVideo(videoData,imageData,videoAlign) {
	if (videoData) {
		
		/*var videopanelLeftPosition = $('#features_submenu ul.feature_stories').width();
		if (videoAlign == 'left') {
			$('#features_submenu ul.feature_stories li.video_panel').css({'left': '' +videopanelLeftPosition + 'px'});
		}
		else {
			$('#features_submenu ul.feature_stories li.video_panel').css({'right': '' +videopanelLeftPosition + 'px'});
		}*/
		$('#feature_video').fadeIn(600, function() {
			/*var so = new SWFObject("/swf/flvplayer.swf", "flvplayer", videoData.width, videoData.height+20, "7", "#c7c7c7", true);
			so.addVariable("file", encodeURIComponent(videoData.src));
			so.addParam("menu", "false");
			so.addParam("wmode","window");
			so.write("feature_video");
			*/
			
			var flashvars = {
			  file: encodeURIComponent(videoData.src)
			};
			var params = {
			  menu: "false",
			  wmode: "window",
			  allowfullscreen: "true"
			};
			var attributes = {
				name: "flvplayer"
			};
			//swfobject.addLoadEvent(forceVideoRedraw);

			swfobject.embedSWF("/swf/flvplayer.swf", "flvplayer", videoData.width, videoData.height + 22, "7.0.0","/swf/expressInstall.swf",flashvars,params,attributes);			
			$('#feature_video').addClass('feature_video_loaded');
			
		})
	} else if (imageData) {
		/*var panelLeftPosition = $('#features_submenu ul.feature_stories').width();
		if (videoAlign == 'left') {
			$('#features_submenu ul.feature_stories li.video_panel').css({'left': '' +panelLeftPosition + 'px'});
		}
		else {
			$('#features_submenu ul.feature_stories li.video_panel').css({'right': '' +panelLeftPosition + 'px'});
		}*/
		var imagePanel = $('<div id="image-panel" class="image-panel-'+videoAlign+'"><img src="'+imageData.src+'" alt="'+imageData.alt+'" width="'+imageData.width+'" height="'+imageData.height+'" /><ul><li><a href="'+imageData.url+'">'+imageData.linktext+'</a></li></ul></div>');
		$('li.video_panel').empty().append(imagePanel);
	}
}

function forceVideoRedraw() {
 $('#flvplayer').focus();
}

function insertNewsStory(position, story) {
	story = story || {};
	
	if (story.finalitem) {
		$(position).append(story.finalitem);
		$(position).addClass('all-items');
	}
	else {
		var link = $('<a></a>');
		if (story.url) {
			link.attr('href', story.url);
		}
		if (story.title) {
			var itemTitle = '<h3>'+ story.title +'</h3>';
			$(position).append(itemTitle);
		}
		if (story.headline) {
			link.text(story.headline);
			$(position).append(link);
		}
	}		

	
}

function showBiographiesFeature(featureData) {
	$('#features_submenu').stop();
	$('#features_submenu').css('top', '196px');
	var biographiesBar = $('<div class="feature_biographies"><ul></ul> <div class="feature_biographies_popup"></div></div><div class="index_link"><a class="full-list">More famous Oxonians</a></div>');
	$('a.full-list', biographiesBar).attr('href', featureData.indexUrl);
	for (i = 0; i < featureData.people.length; i++) {
		insertBiography(featureData.people[i], biographiesBar, (i > 4));
	}
	$('#features_submenu').empty().append(biographiesBar).append('<div id="feature_biographies_popup_area"><div id="feature_biographies_popup"><h3></h3><p></p></div></div>');
	$('#features_submenu').animate({'top':'196px'}, {duration: 500, complete: function() {
		$('#features_submenu').show().animate({'top': '117px'}, {duration: 500});
	}});
}

function insertBiography(personData, biographiesBar, isRightAligned) {
	var person = $('<li><img width="60" height="60" title="" /></li>');
	person.hover(
		function() {
			$(this).addClass('hover');
			if (isRightAligned) {
				$('#feature_biographies_popup').css('left', (this.offsetLeft - 276) + 'px');
			} else {
				$('#feature_biographies_popup').css('left', (this.offsetLeft - 4) + 'px');
			}
			$('#feature_biographies_popup h3').text(personData.name);
			$('#feature_biographies_popup p').text(personData.description);
			$('#feature_biographies_popup').animate({'top': '36px'}, {duration: 500});
		},
		function() {
			$(this).removeClass('hover');
			$('#feature_biographies_popup').stop().css('top', '116px');
		}
	);
	$('img', person).attr('src', personData.image).attr('alt', personData.name);
	$('ul', biographiesBar).append(person);	
}

function showFactsFeature(featureData) {
	$('#features_submenu').stop();
	$('#features_submenu').css('top', '196px');
	var factsBar = $('<div class="feature_facts"><div class="feature_facts_browser"><h3></h3><div class="feature_facts_paging"><a id="feature_fact_previous">Previous fact</a><a id="feature_fact_next">Next fact</a></div><p></p></div></div><div class="index_link"><a class="all-facts">More "Oxford is Unique" facts</a></div>');
	$('a.all-facts', factsBar).attr('href', featureData.indexUrl);
	showFact(featureData.facts, 0, factsBar);
	$('#features_submenu').empty().append(factsBar);
	$('#features_submenu').animate({'top':'196px'}, {duration: 500, complete: function() {
		$('#features_submenu').show().animate({'top': '117px'}, {duration: 500});	
	}});
}
function showFact(facts, i, container) {
	$('h3', container).text(facts[i].title);
	$('p', container).text(facts[i].body);
	if (i > 0) {
		$('#feature_fact_previous', container).removeClass('disabled').animate({opacity: 1}, 1).unbind('click').click(function() {
			showFact(facts, i-1, container);
			return false;
		});
	} else {
		$('#feature_fact_previous', container).addClass('disabled').animate({opacity: 0.5}, 1);
	}
	if (i < facts.length - 1) {
		$('#feature_fact_next', container).removeClass('disabled').animate({opacity: 1}, 1).unbind('click').click(function() {
			showFact(facts, i+1, container);
			return false;
		});
	} else {
		$('#feature_fact_next', container).addClass('disabled').animate({opacity: 0.5}, 1);
	}
}
