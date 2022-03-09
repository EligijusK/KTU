(function(){var q=null,g=true,k=false,o="default",r="edit",n="hide",l="hidebody",c="innerHTML",i="remove",b="save",f="y-menu-show",d="hide-ad",h="pa-disc-less",m="pa-mod-disc-exp",s="pa-add-promo",p="ads:allowed",a="ads:disallowed",e=0,j=-7;YModules.type_pacontainer={body:q,id:"",viewevents:["click"],iframeElement:"",menu:q,moduleapi:q,moduleId:q,moduleContent:q,moduleDefaultView:q,moduleEditView:q,optionsButton:q,paService:q,platform:q,removeDialogConfirmation:q,col2Height:0,intlDisclaimerCtr:q,disclaimerBeacons:q,instrumentationService:q,init:function(u){this.Y=u.getService("yui");this.id=u.getId();this.moduleapi=u;this.platform=u.getService("moduleplatform");this.paService=u.getService("pa");this.instrumentationService=u.getService("instrumentation");var t={viewopen:g,viewclose:g,viewrefresh:g,viewresize:k};this.Y.Object.each(t,function(v,w){this["on"+w]=this.Y.rbind(this.signalChildren,this,w,v);},this);},onviewload:function(y){var w=this.moduleapi,v,x,u=w.getViewNode(y),t=this.platform,z;if(y===o){x=u.query(".pa-menu-optionsbtn");v=w.getService("menu");z=t.getChildModules(this.id);this.body=u;this.optionsButton=x;this.menu=v.create("#"+this.id+"-settings-menu",{focusNode:x});this.menu.subscribe(n,this.hideMenu,this);this.moduleId=w.getContextData("moduleId");this.moduleContent=u.query(".pa-app-col1");this.moduleDefaultView=u.query(".view_default");this.disclaimerBeacons={expand:w.getContextData("disclaimer_expand_beacon"),collapse:w.getContextData("disclaimer_collapse_beacon")};this.Y.Array.each(z,function(A){if(!t.isStarted(A)){t.start(A);}t.signal(A,"viewload",[y]);});this.initIframe();}},onviewunload:function(t){if(t===o){this.showHideIframe(k);this.menu.destroy();this.body=this.iframeElement=this.menu=this.optionsButton=this.moduleDefaultView=this.moduleEditView=this.moduleContent=this.removeDialogConfirmation=this.intlDisclaimerCtr=this.disclaimerBeacons=q;}this.signalChildren(t,"viewunload");},onviewevent:function(A,z){var y=z.target,v=y.get("className").split(/\s+/),w=y.ancestor("A")||y.ancestor("BUTTON"),t=y.getAttribute("href"),x,u=this.body.query("."+s);if(w){v=v.concat(w.get("className").split(/\s+/));t=w.getAttribute("href");}if(u&&(u.compareTo(y)||u.contains(y))){z.preventDefault();this.addPromoApp(u);}if(t&&t.indexOf("#")!==-1){z.preventDefault();}x=v.length;while(x--){switch(v[x]){case"pa-menu-optionsbtn":this.showMenu();break;case"settings-option":this.showSettings();break;case"remove-option":this.showRemoveConfirmation();break;case"help-option":case"expand-option":this.navigate(y);break;case"toggle-fixed":case"toggle-auto":this.toggleFixedHeight();break;case"confirmation-yes":case"try-again":this.removeApp();break;case"confirmation-cancel":this.cancelRemoveApp();break;case"disc-toggle":this.toggleIntlDisclaimer();break;case"disc-confirm":this.confirmIntlDisclaimer();break;case"pa-mod-show-disc":this.toggleAppDisclaimer(y);break;}}},onmessage:function(t,u,v){this.showHideIframe(t==p);},initIframe:function(){var u=this.moduleapi,t=this.body.query(".pa-app-col2"),v,x,w=this.Y;if(t){this.column2=t;v=t.getElementsByTagName("iframe");x=v.item(0)&&w.Node.getDOMNode(v.item(0));if(x){u.listen(p);u.listen(a);if(w.UA.webkit&&!x.contentWindow){x.src=x.src;}w.Event.attach("load",this.iframeOnLoad,x,this,x);this.iframeElement=x;}}},showHideIframe:function(x){var u=this.column2,z=this.iframeElement,t,w,y=this.Y,v;if(!u||!z||!this.moduleapi.isViewOpen(o)){return;}t=z.contentWindow;if(x){u.removeClass(d);w=t.document.body;t.clickHandle=y.Event.attach("click",this.iframeOnClick,w,this,t);}else{u.addClass(d);if(t&&t.clickHandle){t.clickHandle.detach();}}if(y.UA.ie<8){v=z.style;v.visibility="hidden";v.visibility="";}},iframeOnLoad:function(t,u){if(u){this.Y.Event.detach("load",this.iframeOnLoad,u);if(u==this.iframeElement){this.showHideIframe(g);}}},iframeOnClick:function(w,u){var v=this.paService,t=u&&u.clickHandle;if(w.target.hasClass("open-gallery")){if(t){t.detach();u.clickHandle=q;}if(v){v.openAppGallery();}}},signalChildren:function(z,y,A){var B=true,w,u=this.platform,t=u.getChildModules(this.id),x=t.length,v;for(v=0;v<x;v++){w=u.signal(t[v],y,[z]);if(!w&&A){B=false;break;}}return B;},addPromoApp:function(u){var w=u.getAttribute("mid"),t=this.paService.getCurrentModuleButton().getAttribute("data-ad-a-b"),v={_id:this.id,type:"module",args:{_action:b,_subAction:"add",_container:0,type:"pacontainer",modId:w,coke:this.platform.getModuleApi(w).getContextData("_coke")}},x={success:this.onAddPromoAppSuccess,failure:this.onAddPromoAppFailure,scope:this};if(t){v.args.addBeacon=t;}this.moduleapi.makeRequest(v,x);},onAddPromoAppSuccess:function(v,y){var x=this.moduleapi,A=this.paService,z=v.data,u=z.saveMsg,w=z.saveStatus,t=z.addBeacon;if(A){if(w==e){A.refreshUserList(z.saveTS);}if(x.isViewOpen(o)){switch(w){case e:A.collapse();break;case j:A.showAppError(u);break;default:this.body.query(".pa-add-promo-cont").set(c,u);break;}}if(t){A.fireExternalBeacon(t);}}},onAddPromoAppFailure:function(t,u){},showMenu:function(){var y=this.Y,w,x=this.optionsButton,v=y.DOM.region(y.Node.getDOMNode(x)),u=(this.moduleapi.getViewDirection(o)==="rtl"),t={top:v.bottom-v.top+1};if(this.Y.UA.ie==6){w=this.platform.getModuleApi(this.moduleId);t[u?"left":"right"]=(w.getProperty("type")=="yservices")?25:-1;}else{t[u?"right":"left"]=0;}this.menu.show(t);x.addClass(f);},hideMenu:function(){this.optionsButton.removeClass(f);},showSettings:function(){var t=this.platform,w=this.paService,u=this.removeDialogConfirmation,v=this.moduleId;w.setModuleLoading(g);if(u){u.addClass(n);this.moduleDefaultView.removeClass(l);}if(t.isViewOpen(v,r)){t.closeView(v,r,this.Y.bind(function(){w.setModuleLoading(k);this.fixColumnHeightForIntlDisclaimer();},this));}else{t.openView(v,r,this.Y.bind(function(){this.fixColumnHeightForIntlDisclaimer();},this));}},showRemoveConfirmation:function(){var t={_id:this.id,type:"module",args:{_action:"show",_subAction:i,type:"pacontainer",modId:this.moduleId}},u={success:this.showRemoveConfirmationSuccess,failure:this.showRemoveConfirmationFailure,scope:this};if(!this.removeDialogConfirmation){this.moduleapi.makeRequest(t,u);}else{if(this.removeDialogConfirmation.hasClass(n)){this.removeDialogConfirmation.removeClass(n);this.toggleModuleBody();}}},showRemoveConfirmationSuccess:function(t){var u=t.data.html;this.removeDialogConfirmation=this.Y.Node.create(u);this.moduleContent.appendChild(this.removeDialogConfirmation);this.toggleModuleBody();},toggleModuleBody:function(){var t=this.moduleEditView||this.body.query(".view_edit");if(t){t.toggleClass(l);}else{this.moduleDefaultView.toggleClass(l);}},navigate:function(u){var v=u.get("nodeName"),t;if(v!=="A"){u=u.ancestor("A");}t=u.getAttribute("href");this.moduleapi.navigate(t);},showRemoveConfirmationFailure:function(t){},cancelRemoveApp:function(){this.removeDialogConfirmation.addClass(n);this.toggleModuleBody();},removeApp:function(){var w,x,v=this.moduleId,u=0,t=this.body.query(".remove-all input");if(t){u=t.get("checked")?1:0;}w={_id:this.id,type:"module",args:{_action:b,_subAction:i,type:"pacontainer",modId:v,removeAll:u}};x={success:this.removeAppSuccess,failure:this.removeAppFailure,scope:this,argument:{modId:v}};this.moduleapi.makeRequest(w,x);},removeAppSuccess:function(u,v){var w=u.data,t=this.platform,x=v.modId;if(w.status===0||w.status===200){this.paService.collapse();this.paService.refreshUserList(w.saveTS);t.stop(x);t.unregisterModule(x);}},removeAppFailure:function(t){var u=this.moduleapi.getContextData("html"),v=this.removeDialogConfirmation.one(":first-child");v.set(c,u);v.replaceClass("confirmation-screen","remove-errormsg");},toggleAppDisclaimer:function(t){var u=t.ancestor(".pa-mod-disc");u.toggleClass(m);if(u.hasClass(m)){u.one("p").focus();}else{t.focus();}},toggleIntlDisclaimer:function(){var v=this.intlDisclaimerCtr||this.body.one(".pa-disclaimer-intl"),x=this.moduleEditView||this.body.one(".view_edit"),w=x||this.moduleDefaultView,u=this.instrumentationService,t=this.disclaimerBeacons;if(v){if(w.ancestor(".ua-ff")){w.setStyle("height","100px");}if(v.hasClass(h)){v.removeClass(h);v.one("p").focus();u.fireBeacon(t.expand);}else{v.addClass(h);u.fireBeacon(t.collapse);}this.intlDisclaimerCtr=v;this.fixColumnHeightForIntlDisclaimer();}},confirmIntlDisclaimer:function(){var t={_id:this.id,type:"module",args:{_action:b,_subAction:"confdisc",_container:0,type:"pacontainer",modId:this.moduleId}},u={scope:this};this.hideIntlDisclaimer();this.moduleapi.makeRequest(t,u);},hideIntlDisclaimer:function(t){var u=this.intlDisclaimerCtr||this.body.one(".pa-disclaimer-intl");if(u){u.addClass("pa-disc-hide");this.intlDisclaimerCtr=u;this.fixColumnHeightForIntlDisclaimer();u.get("parentNode").removeClass("show-pa-disclaimer");u.get("parentNode").removeChild(u);}},fixColumnHeightForIntlDisclaimer:function(){var t=this.Y,C=t.DOM,B=t.Node,z=this.intlDisclaimerCtr,v,y,w,A,u,x;if(z){v=C.region(B.getDOMNode(z)).height;y=B.getDOMNode(this.body.one(".pa-app-col2"));w=this.col2Height||C.region(y).height;A=this.moduleEditView||this.body.one(".view_edit")||this.moduleDefaultView;u=B.getDOMNode(this.body.query("."+s));x=u?C.region(u).height:0;this.col2Height=w;A.setStyle("height",w-v-x+"px");}}};})();/* Copyright (c) 2012, Yahoo! Inc.  All rights reserved. */