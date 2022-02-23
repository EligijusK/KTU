(function() {

  /*                            *

        Inline Library Code
        - Qwery
        - Bean

  *                             */

/*!
 * Qwery - A Blazing Fast query selector engine
 * https://github.com/ded/qwery
 * copyright Dustin Diaz & Jacob Thornton 2011
 * MIT License
*/
!function(a,b){function _(a,c){var d=typeof c=="string"?_(c)[0]:c||b;if(!d||!a)return[];if(h=Y(a,c,_))return h;return bb(a,d)}function $(a){var b=[],c,d;label:for(c=0;c<a.length;c++){for(d=0;d<b.length;d++)if(b[d]==a[c])continue label;b[b.length]=a[c]}return b}function Z(a){return a&&a.nodeType&&(a.nodeType==1||a.nodeType==9)}function Y(a,c,d){var e=typeof c=="string"?d(c)[0]:c||b;if(a===window||Z(a))return!c||a!==window&&Z(e)&&ba(a,e)?[a]:[];if(a&&typeof a=="object"&&isFinite(a.length))return R(a);if(h=a.match(x))return(m=b.getElementById(h[1]))?[m]:[];if(h=a.match(z))return R(e.getElementsByTagName(h[1]));return!1}function X(a){var c=[],d=[],e,f=0,g,h,i,j,k,l,m,n,o,q,r,s=Q.g(a)||Q.s(a,a.split(F)),t=a.match(E),u;s=s.slice(0);if(!s.length)return c;k=s.pop(),n=s.length&&(i=s[s.length-1].match(x))?b.getElementById(i[1]):b;if(!n)return c;o=T(k),m=t&&/^[+~]$/.test(t[t.length-1])?function(a){while(n=n.nextSibling)n.nodeType==1&&(o[1]?o[1]==n.tagName.toLowerCase():1)&&a.push(n);return a}([]):n.getElementsByTagName(o[1]||"*");for(e=0,h=m.length;e<h;e++)if(q=U.apply(m[e],o))c[f++]=q;if(!s.length)return c;for(f=0,h=c.length,g=0;f<h;f++){j=c[f];for(e=s.length;e--;)while(j=L[t[e]](j,c[f]))if(p=U.apply(j,T(s[e])))break;p&&(d[g++]=c[f])}return d}function W(a,b,c){switch(a){case"=":return b==c;case"^=":return b.match(P.g("^="+c)||P.s("^="+c,new RegExp("^"+V(c))));case"$=":return b.match(P.g("$="+c)||P.s("$="+c,new RegExp(V(c)+"$")));case"*=":return b.match(P.g(c)||P.s(c,new RegExp(V(c))));case"~=":return b.match(P.g("~="+c)||P.s("~="+c,new RegExp("(?:^|\\s+)"+V(c)+"(?:\\s+|$)")));case"|=":return b.match(P.g("|="+c)||P.s("|="+c,new RegExp("^"+V(c)+"(-|$)")))}return 0}function V(a){return O.g(a)||O.s(a,a.replace(G,"\\$1"))}function U(a,b,c,e,f,g,h,j,k,l,m){var n,o,p;if(b&&this.tagName.toLowerCase()!==b)return!1;if(c&&(n=c.match(v))&&n[1]!==this.id)return!1;if(c&&(q=c.match(w)))for(d=q.length;d--;){o=q[d].slice(1);if(!(N.g(o)||N.s(o,new RegExp("(^|\\s+)"+o+"(\\s+|$)"))).test(this.className))return!1}if(k&&_.pseudos[k]&&!_.pseudos[k](this,m))return!1;if(e&&!h){i=this.attributes;for(p in i)if(Object.prototype.hasOwnProperty.call(i,p)&&(i[p].name||p)==f)return this}if(e&&!W(g,this.getAttribute(f)||"",h))return!1;return this}function T(a){return a.match(K)}function S(a){while(a=a.previousSibling)if(a.nodeType==1)break;return a}function R(a){k=[];for(d=0,o=a.length;d<o;d++)k[d]=a[d];return k}function M(){this.c={}}var c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u=b.documentElement,v=/#([\w\-]+)/,w=/\.[\w\-]+/g,x=/^#([\w\-]+$)/,y=/^\.([\w\-]+)$/,z=/^([\w\-]+)$/,A=/^([\w]+)?\.([\w\-]+)$/,B=/\s*([\s\+\~>])\s*/g,C=/[\s\>\+\~]/,D=/(?![\s\w\-\/\?\&\=\:\.\(\)\!,@#%<>\{\}\$\*\^'"]*\])/,E=new RegExp("("+C.source+")"+D.source,"g"),F=new RegExp(C.source+D.source),G=/([.*+?\^=!:${}()|\[\]\/\\])/g,H=/^([a-z0-9]+)?(?:([\.\#]+[\w\-\.#]+)?)/,I=/\[([\w\-]+)(?:([\|\^\$\*\~]?\=)['"]?([ \w\-\/\?\&\=\:\.\(\)\!,@#%<>\{\}\$\*\^]+)["']?)?\]/,J=/:([\w\-]+)(\(['"]?(\w+)['"]?\))?/,K=new RegExp(H.source+"("+I.source+")?"+"("+J.source+")?"),L={" ":function(a){return a&&a!==u&&a.parentNode},">":function(a,b){return a&&a.parentNode==b.parentNode&&a.parentNode},"~":function(a){return a&&a.previousSibling},"+":function(a,b,c,d){if(!a)return!1;c=S(a),d=S(b);return c&&d&&c==d&&c}};M.prototype={g:function(a){return this.c[a]||undefined},s:function(a,b){this.c[a]=b;return b}};var N=new M,O=new M,P=new M,Q=new M,ba="compareDocumentPosition"in u?function(a,b){return(b.compareDocumentPosition(a)&16)==16}:"contains"in u?function(a,c){c=c==b||c==window?u:c;return c!==a&&c.contains(a)}:function(a,b){while(a=a.parentNode)if(a===b)return 1;return 0},bb=b.querySelector&&b.querySelectorAll?function(a,c){if(b.getElementsByClassName&&(h=a.match(y)))return R(c.getElementsByClassName(h[1]));return R(c.querySelectorAll(a))}:function(a,c){a=a.replace(B,"$1");var d=[],f,i=[],j;if(h=a.match(A)){s=c.getElementsByTagName(h[1]||"*"),k=N.g(h[2])||N.s(h[2],new RegExp("(^|\\s+)"+h[2]+"(\\s+|$)"));for(j=0,g=s.length,e=0;j<g;j++)k.test(s[j].className)&&(d[e++]=s[j]);return d}for(j=0,s=a.split(","),g=s.length;j<g;j++)i[j]=X(s[j]);for(j=0,g=i.length;j<g&&(f=i[j]);j++){var l=f;if(c!==b){l=[];for(e=0,h=f.length;e<h&&(element=f[e]);e++)ba(element,c)&&l.push(element)}d=d.concat(l)}return $(d)};_.uniq=$,_.pseudos={};var bc=a.qwery;_.noConflict=function(){a.qwery=bc;return this},a.qwery=_}(this,document)

/*!
  * bean.js - copyright Jacob Thornton 2011
  * https://github.com/fat/bean
  * MIT License
  * special thanks to:
  * dean edwards: http://dean.edwards.name/
  * dperini: https://github.com/dperini/nwevents
  * the entire mootools team: github.com/mootools/mootools-core
*/
!function(a){function F(a){var b=a.relatedTarget;return b?b!=this&&b.prefix!="xul"&&!/document/.test(this.toString())&&!p(this,b):b===null}var b=1,c={},d={},e=/over|out/,f=/[^\.]*(?=\..*)\.|.*/,g=/\..*/,h="addEventListener",i="attachEvent",j="removeEventListener",k="detachEvent",l=a.document||{},m=l.documentElement||{},n=m[h],o=n?h:i,p=function(a,b){var c=b.parentNode;while(c!==null){if(c==a)return!0;c=c.parentNode}},q=function(a,c){return a.__uid=c&&c+"::"+b++||a.__uid||b++},r=function(a){var b=q(a);return c[b]=c[b]||{}},s=n?function(a,b,c,d){a[d?h:j](b,c,!1)}:function(a,b,c,d,e){e&&d&&(a["_on"+e]=a["_on"+e]||0),a[d?i:k]("on"+b,c)},t=function(b,c,d){return function(e){e=D(e||((this.ownerDocument||this.document||this).parentWindow||a).event);return c.apply(b,[e].concat(d))}},u=function(a,b,c,d,e){return function(f){(d?d.apply(this,arguments):n?!0:f&&f.propertyName=="_on"+c||!f)&&b.apply(a,Array.prototype.slice.call(arguments,f?0:1).concat(e))}},v=function(a,b,c,e){var h=b.replace(g,""),i=r(a),j=i[h]||(i[h]={}),k=c,l=q(c,b.replace(f,""));if(j[l])return a;var m=G[h];m&&(c=m.condition?u(a,c,h,m.condition):c,h=m.base||h);var p=E[h];c=p?t(a,c,e):u(a,c,h,!1,e),p=n||p;if(h=="unload"){var v=c;c=function(){w(a,h,c)&&v()}}a[o]&&s(a,p?h:"propertychange",c,!0,!p&&h),j[l]=c,c.__uid=l,c.__originalFn=k;return h=="unload"?a:d[q(a)]=a},w=function(a,b,c){function l(b){c=j[k][b];if(!!c){delete j[k][b];if(a[o]){k=G[k]?G[k].base:k;var d=n||E[k];s(a,d?k:"propertychange",c,!1,!d&&k)}}}var d,e,h,i,j=r(a),k=b.replace(g,"");if(!j||!j[k])return a;e=b.replace(f,""),h=e?e.split("."):[c.__uid],l(e);for(i=h.length;i--;l(h[i]));return a},x=function(a,b,c){return function(d){var e=typeof a=="string"?c(a,this):a;for(var f=d.target;f&&f!=this;f=f.parentNode)for(var g=e.length;g--;)if(e[g]==f)return b.apply(f,arguments)}},y=function(a,b,c,d,e){if(typeof b=="object"&&!c)for(var f in b)b.hasOwnProperty(f)&&y(a,f,b[f]);else{var g=typeof c=="string",h=(g?c:b).split(" ");c=g?x(b,d,e):c;for(var i=h.length;i--;)v(a,h[i],c,Array.prototype.slice.call(arguments,g?4:3))}return a},z=function(a,b,c){var d,e,h,i,j,k=typeof b=="string",l=k&&b.replace(f,""),l=l&&l.split("."),m=w,n=r(a);if(k&&/\s/.test(b)){b=b.split(" "),j=b.length-1;while(z(a,b[j])&&j--);return a}i=k?b.replace(g,""):b;if(!n||l||k&&!n[i]){for(d in n)if(n.hasOwnProperty(d))for(j in n[d])for(e=l.length;e--;)n[d].hasOwnProperty(j)&&(new RegExp("^"+l[e]+"::\\d*(\\..*)?$")).test(j)&&m(a,[d,j].join("."));return a}if(typeof c=="function")m(a,i,c);else if(l)m(a,b);else{m=i?m:z,h=k&&i,i=i?c||n[i]||i:n;for(d in i)i.hasOwnProperty(d)&&(m(a,h||d,i[d]),delete i[d])}return a},A=function(a,b,c){var d,e,h,i,j=b.split(" ");for(h=j.length;h--;){b=j[h].replace(g,"");var k=E[b],l=j[h].replace(f,""),m=r(a)[b];if(l){l=l.split(".");for(e=l.length;e--;)for(i in m)m.hasOwnProperty(i)&&(new RegExp("^"+l[e]+"::\\d*(\\..*)?$")).test(i)&&m[i].apply(a,[!1].concat(c))}else if(!c&&a[o])B(k,b,a);else for(e in m)m.hasOwnProperty(e)&&m[e].apply(a,[!1].concat(c))}return a},B=n?function(b,c,d){evt=document.createEvent(b?"HTMLEvents":"UIEvents"),evt[b?"initEvent":"initUIEvent"](c,!0,!0,a,1),d.dispatchEvent(evt)}:function(a,b,c){a?c.fireEvent("on"+b,document.createEventObject()):c["_on"+b]++},C=function(a,b,c){var d=r(b),e,f,g=q(a);e=c?d[c]:d;for(f in e)e.hasOwnProperty(f)&&(c?y:C)(a,c||b,c?e[f].__originalFn:f);return a},D=function(a){var b={};if(!a)return b;var c=a.type,d=a.target||a.srcElement;b.preventDefault=D.preventDefault(a),b.stopPropagation=D.stopPropagation(a),b.target=d&&d.nodeType==3?d.parentNode:d;if(~c.indexOf("key"))b.keyCode=a.which||a.keyCode;else if(/click|mouse|menu/i.test(c)){b.rightClick=a.which==3||a.button==2,b.pos={x:0,y:0};if(a.pageX||a.pageY)b.clientX=a.pageX,b.clientY=a.pageY;else if(a.clientX||a.clientY)b.clientX=a.clientX+document.body.scrollLeft+document.documentElement.scrollLeft,b.clientY=a.clientY+document.body.scrollTop+document.documentElement.scrollTop;e.test(c)&&(b.relatedTarget=a.relatedTarget||a[(c=="mouseover"?"from":"to")+"Element"])}for(var f in a)f in b||(b[f]=a[f]);return b};D.preventDefault=function(a){return function(){a.preventDefault?a.preventDefault():a.returnValue=!1}},D.stopPropagation=function(a){return function(){a.stopPropagation?a.stopPropagation():a.cancelBubble=!0}};var E={click:1,dblclick:1,mouseup:1,mousedown:1,contextmenu:1,mousewheel:1,DOMMouseScroll:1,mouseover:1,mouseout:1,mousemove:1,selectstart:1,selectend:1,keydown:1,keypress:1,keyup:1,orientationchange:1,touchstart:1,touchmove:1,touchend:1,touchcancel:1,gesturestart:1,gesturechange:1,gestureend:1,focus:1,blur:1,change:1,reset:1,select:1,submit:1,load:1,unload:1,beforeunload:1,resize:1,move:1,DOMContentLoaded:1,readystatechange:1,error:1,abort:1,scroll:1},G={mouseenter:{base:"mouseover",condition:F},mouseleave:{base:"mouseout",condition:F},mousewheel:{base:/Firefox/.test(navigator.userAgent)?"DOMMouseScroll":"mousewheel"}},H={add:y,remove:z,clone:C,fire:A},I=function(a){var b=z(a).__uid;b&&(delete d[b],delete c[b])};a[i]&&y(a,"unload",function(){for(var b in d)d.hasOwnProperty(b)&&I(d[b]);a.CollectGarbage&&CollectGarbage()});var J=a.bean;H.noConflict=function(){a.bean=J;return this},typeof module!="undefined"&&module.exports?module.exports=H:a.bean=H}(this)

/*
  A short (minifed) chunk of code to provide a cross-browser domready event
  The technique is nabbed from: https://github.com/dperini/ContentLoaded/
*/
function contentLoaded(a,b){var c=!1,d=!0,e=a.document,f=e.documentElement,g=e.addEventListener?"addEventListener":"attachEvent",h=e.addEventListener?"removeEventListener":"detachEvent",i=e.addEventListener?"":"on",j=function(d){if(d.type!="readystatechange"||e.readyState=="complete")(d.type=="load"?a:e)[h](i+d.type,j,!1),!c&&(c=!0)&&b.call(a,d.type||d)},k=function(){try{f.doScroll("left")}catch(a){setTimeout(k,50);return}j("poll")};if(e.readyState=="complete")b.call(a,"lazy");else{if(e.createEventObject&&f.doScroll){try{d=!a.frameElement}catch(l){}d&&k()}e[g](i+"DOMContentLoaded",j,!1),e[g](i+"readystatechange",j,!1),a[g](i+"load",j,!1)}}

  /*                      *

        The Injection

  *                       */

// Enable styling of the html5 time tag in IE6/7 (this is used within the breadcrumb system)
document.createElement('time');

// Detect CSS pointer-events support
if ('pointerEvents' in document.documentElement.style) {
  setTimeout(function() { document.getElementsByTagName("html")[0].className += " pointerevents" }, 500);
}

// Main global variable references
var injectionDomain = "",
    EMPYREAN = "//brand.unimelb.edu.au/global";

// Helper methods
var helpers = {
  // A simple string replacement templating method
  interpolate: function (string, o) {
    return string.replace(/\{([^{}]*)\}/g,
      function (a, b) {
        var r = o[b];
        return typeof r === 'string' || typeof r === 'number' ? r : a;
      }
    );
  },
  // Find the absolute path of the current page
  getDomain: function() {
    if (injectionDomain) return injectionDomain;
    // Use the script tag of the current javascript file (injection.js) to
    // find the full url of the current page
    var re = new RegExp(/\/js\/injection\.js.*/);
    var scripts = document.getElementsByTagName('script');

    for (var i = 0, ii = scripts.length; i < ii; i++) {
      var path = scripts[i].getAttribute('src');
      if(re.test(path)) injectionDomain = path.replace(re, '');
    }
    return injectionDomain;
  },
  getAbsolutePath: function(path) {
    if (!path) path = ""
    return helpers.getDomain() + path;
  },
  // A shortcut for creating html elements by passing an object filled with properties
  createElement: function(tag, attr) {
    var key, elem = document.createElement(tag);
    for (key in attr) {
      if (key === 'className') elem['class'] = attr[key];
      elem[key] = attr[key];
    }
    return elem;
  },
  // Takes a html string and converts it into a fragment of html nodes
  createNodes: function(text) {
    var frag = document.createDocumentFragment(),
        container = document.createElement('div');
    container.innerHTML = text;
    for (var children = container.childNodes; children.length > 0;
      frag.appendChild(container.removeChild(children[0])));
    return frag;
  },
  // A cross-browser implementation of getComputedStyle
  getComputedStyle: function(elem, styleProp) {
    if (typeof elem.currentStyle !== 'undefined') {
      // IE has no backgroundPosition value
      if (styleProp == 'backgroundPosition') {
        return elem.currentStyle['backgroundPositionX'] + ' ' +
               elem.currentStyle['backgroundPositionY'];
      }
      return elem.currentStyle[styleProp]
    } else {
      try { return document.defaultView.getComputedStyle(elem, null)[styleProp]; }
      catch(e) { return false; }
    }
  }
}

var headerMarkup = function() {
  return helpers.interpolate('\
    <div id="g-header" role="banner"> \
      <div class="wrapper"> \
        <ul class="skiplinks"> \
          <li><a href="#g-global-menu">Skip to navigation</a></li> \
          <li><a href="#main-content">Skip to content</a></li> \
        </ul> \
        <ol id="g-breadcrumb-menu"> \
          <li id="g-breadcrumb-home"><a href="http://www.unimelb.edu.au/">University Home</a></li> \
        </ol> \
        <div id="g-global-search"> \
          <a href="#" id="g-search-button" role="button" aria-haspopup="true">Search</a> \
          <div id="g-global-search-overlay"> \
            <form method="get" action="http://search.unimelb.edu.au/" name="g-searchform" id="g-searchform" role="search"> \
              <label for="q"><span>Search the university</span></label> \
              <input accesskey="s" type="search" name="q" id="q" value="" title="Search the University"> \
              <input type="submit" name="sa" id="g-global-search-submit" value="Search"> \
            </form> \
          </div> \
        </div> \
        <hr> \
        <ol id="g-global-menu" role="navigation" aria-labelledby="g-global-menu-label"> \
          <a id="g-global-menu-logo" href="http://www.unimelb.edu.au">The University of Melbourne</a> \
          <li id="g-global-menu-label">Browse the University</li> \
          <li><a href="{globalPath}/study.html" id="g-global-menu-study">Study</a></li> \
          <li><a href="http://www.unimelb.edu.au/research/" id="g-global-menu-research">Research</a></li> \
          <li><a href="{globalPath}/community.html" id="g-global-menu-community">Community</a></li> \
          <li><a href="{globalPath}/contact-maps.html" id="g-global-menu-contact-maps" class="last">Contact &amp; Maps</a></li> \
        </ol> \
        <hr class="g-clear-floats"> \
      </div> \
    </div> \
    <div id="g-header-blue-block"></div> \
    <span class="g-clear-floats"></span>', { path: helpers.getAbsolutePath(), globalPath: EMPYREAN, current_url: window.location.href });
}

var footerMarkup = function() {
  return helpers.interpolate('\
    <div id="footernav" role="contentinfo"> \
      <div class="wrapper"> \
        <p id="backtotop"><a href="#g-breadcrumb-menu">Back to top</a></p> \
        <a href="http://unimelb.edu.au" id="footernav-logo"><img src="{path}/images/unimelb-logo-lge.png" alt="The University of Melbourne logo"></a> \
        <dl id="footernav-required"> \
          <dt>Phone:</dt> \
          <dd>13 MELB (13 6352) | International: +(61 3) 9035 5511</dd><br> \
          <dt>The University of Melbourne ABN:</dt> \
          <dd>84 002 705 224</dd><br> \
          <dt>CRICOS Provider Code:</dt> \
          <dd>00116K <a href="http://www.services.unimelb.edu.au/international/visas/index.html">(visa information)</a></dd><br> \
          <dt>Web brand guidelines created by</dt> \
          <dd><a href="http://marcom.unimelb.edu.au">Marketing &amp; Communications</a></dd> \
        </dl> \
        <p id="footer-connect-title">Connect with The University&nbsp;of&nbsp;Melbourne</p> \
        <ul id="footernav-social"> \
          <li><a href="http://twitter.com/unimelb"><img src="{path}/images/icon-twitter.png" alt="Connect with us on Twitter"></a></li> \
          <li><a href="http://facebook.com/melbuni"><img src="{path}/images/icon-facebook.png" alt="Connect with us on Facebook"></a></li> \
        </ul> \
        <ul id="footernav-legals"> \
          <li><a href="http://www.unimelb.edu.au/disclaimer/">Disclaimer &amp; copyright</a></li> \
          <li><a href="http://www.unimelb.edu.au/accessibility/index.html">Accessibility</a></li> \
          <li><a href="http://www.unimelb.edu.au/disclaimer/privacy.html">Privacy</a></li> \
        </ul> \
      </div> \
      <hr class="g-clear-floats"> \
    </div>', { path: helpers.getAbsolutePath() });
}

// We want the width of our header & footer to watch the width of the page
// this is slightly trickier than you would imagine...
var resizeWrapper = function() {
  var widthVal = false;
  // If body directly contains a wrapper class, then we assume this site is
  // using the templates, so we can grab the container width directly
  var templateWrapper = qwery('body > .wrapper');
  if (!!templateWrapper.length) {
    // If the template wrapper already has a maxWidth value set, then we don't
    // want to override it. Setting widthVal to false will bypass the maxWidth
    // re-allocation below
    // Also, use the last banner as our reference point, as within the
    // homepage, the first banner is set to 100% width
    widthVal = templateWrapper[templateWrapper.length - 1].offsetWidth < 960 ? false : templateWrapper[templateWrapper.length - 1].offsetWidth + 'px';
  } else {
    // Otherwise, we need to find the main wrapping tag and calculate
    // its width. New containers should be added to this query list.
    var query = 'body > #wrapper, body > #container, body > #page, body > .container, body > .home > .container, body #pagecontainer, body > #wrap, body > #allContent, body > .backdrop > .container',
        matches = qwery(query),
        selectors = query.split(/,\s*/);
    for (var i = 0, ii = matches.length; i < ii; i++) {
      // Find the selector (from the list above) which matched
      var matchingSelector = (function(_this) {
        for (var i in selectors) {
          if (_this == qwery(selectors[i])[0]) return selectors[i];
        }
        return false;
      })(matches[i]);

      // If the window width is wide enough to detect a fixed width (> 960),
      // look for an offsetLeft value on the container. Otherwise, assume a
      // fluid width site
      var wrapper = qwery(matchingSelector)[0],
          windowWidth = typeof(window.innerWidth) == 'number'
                        ? window.innerWidth
                        : (document.documentElement && document.documentElement.clientWidth)
                          || (document.body && document.body.clientWidth);
      if (windowWidth > 960 && wrapper.offsetLeft > 5) widthVal = wrapper.offsetWidth + 'px';
      else widthVal = 'auto';

      // Manually set widths of the header/footer wrappers to match the body width
      var elements = qwery('#g-header .wrapper, #footernav .wrapper');
      for (var i = 0, ii = elements.length; i < ii; i++) {
        elements[i].style.width = widthVal;
        elements[i].style.margin = '0px auto';
      }
      break;
      // If the footer spans the entire width of the page, then we need to
      // add some extra padding to the sides
      if (!widthVal || widthVal == 'auto' || widthVal == '100%') {
        var footerWrapper = qwery('#footernav .wrapper')[0];
        footerWrapper.style.width = '97%';
        footerWrapper.style.marginLeft = 'auto';
        footerWrapper.style.marginRight = 'auto';
      }
    }
  }

  // If maxWidth is supported, use that instead of width
  if (document.createElement('detect').style.maxWidth === "" && widthVal !== false) {
    var wrappers = qwery('#g-header > .wrapper, #footernav > .wrapper');
    for (var i = 0, ii = wrappers.length; i < ii; i++) {
      // IE errors on .maxWidth so provide ['max-width'] as a fallback
      try { wrappers[i].style.maxWidth = widthVal; }
      catch(e) { wrappers[i].style['max-width'] = widthVal; }
      wrappers[i].style.width = 'auto';
    }
  }
}

var attachEvents = function() {
  // Show/hide search methods
  var showSearch = function() {
    var container = qwery('#g-global-search-overlay')[0];
    container.className = 'active';
    qwery('input', container)[0].focus();
  };
  var hideSearch = function(e) {
    var container = qwery('#g-global-search-overlay')[0];
    container.className = '';
  }

  bean.add(qwery('body')[0], 'click', function(e) {
    if (e.target.id === 'g-search-button') {
      e.preventDefault();
      showSearch();
    }
  });
  bean.add(qwery('body')[0], 'click', function(e) {
    // Only hide the search box if a click event occurs outside it's wrapper
    if (e.target.id !== 'g-search-button' &&
        e.target.id !== 'g-global-search-overlay' &&
        e.target.id !== 'g-searchform' &&
        e.target.parentNode.id !== 'g-searchform' &&
        e.target.parentNode.parentNode.id !== 'g-searchform') {
      hideSearch();
    }
  });
  bean.add(document, 'keydown', function(e) {
    var unicode = e.charCode ? e.charCode : e.keyCode;
    if (unicode == 191) {
      if ((/input|textarea/i).test(e.target.nodeName)) return;
      showSearch();
      e.preventDefault();
      return false;
    } else if (unicode == 27) {
      hideSearch();
    }
  });
}

// Push the document body down to fit the header
var pushHeader = function() {
  var topVal = parseInt(document.body.style.top);
  topVal = isNaN(topVal) ? 0 : topVal;
  document.body.style.position = 'relative';
  document.body.style.top = 60 + topVal + 'px';
  topVal = parseInt(document.body.style.top);
}

var pushHeaderBackground = function() {
  var bgPosition = helpers.getComputedStyle(document.body, 'backgroundPosition');
  bgPosition = bgPosition.split(/\s/);
  if (!parseInt(bgPosition[1])) bgPosition[1] = '0px';
  document.body.style.backgroundPosition = bgPosition[0] + ' ' + (parseInt(bgPosition[1]) + 60) + 'px';
}

// Load required css file in dynamically
// To get around potential load issues, it should also be linked to directly
// from the containing page
var loadCSS = function(href) {
  var url = helpers.getDomain() + '/css/style.css',
      re = new RegExp(url),
      s = helpers.createElement('link', { href: url, rel: 'stylesheet', type: 'text/css' });
  if (re.test(document.getElementsByTagName('head')[0].innerHTML)) return;
  document.getElementsByTagName('head')[0].appendChild(s);
}

// Inject the markup for the header & footer into the page
var loadContent = function() {
  var body = document.body,
      header = helpers.createNodes(headerMarkup()),
      footer = helpers.createNodes(footerMarkup());
  // Inject the header and footer markup
  body.insertBefore(header, body.firstChild);
  body.appendChild(footer);
}

function breadCrumbs() {
  var bc = helpers.createElement('script', {
    type: 'text/javascript',
    async: 'async',
    src: helpers.getAbsolutePath()+"/js/breadcrumbs.js"
  });
  var s = document.getElementsByTagName('script')[0];
  s.parentNode.insertBefore(bc, s);
}

// Initialisation
var markupInit = function() {
  loadCSS();
  pushHeader();
  pushHeaderBackground();
  loadContent();
  resizeWrapper();
  attachEvents();
  breadCrumbs();
}


// domContentReady event
contentLoaded(this, markupInit);

})();

/*                  *

      Analytics

*                   */

var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-21795369-1']);
_gaq.push(['_setDomainName', '.unimelb.edu.au']);
// _gaq.push(['_setAllowHash', false]);
_gaq.push(['_trackPageview']);
(function() {
  var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
  ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
  var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();