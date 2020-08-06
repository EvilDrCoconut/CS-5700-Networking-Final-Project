/* ***************************************************************************
The following are globally accessible functions that can be used in any other
script file that extends the functionality for any specific page, etc.
*************************************************************************** */
// get the height of the content area on the page
function getContentAreaHeight(){
// contentAreaHeight = (parseInt($(window).height()) - parseInt($('header').outerHeight()) - parseInt($('footer').outerHeight()));
contentAreaHeight=parseInt($(window).height())-parseInt($("header").outerHeight())}
// we need to pass the browser window size to PHP so that we can better tailor responsive imagery all around
function getWindowSize(){
// windowSize[0] = $(window).height();
// windowSize[1] = $(window).width();
$.post("/wp-content/themes/nudev/src/windowsize.php",{height:$(window).height(),width:$(window).width()},function(e){
// console.log(data);
})}
/* ************************************************************************ */var windowSize=Array(0,0),rotators=null,iamnavbgs=null,contentAreaHeight=0,debug=!1,showSize=!1,sizeBreak=900,isSafari=/safari/i.test(navigator.userAgent),windowWidth=null,offset=0,exceedsContainer=!1;
/* ************************************************************************ */
!function(e,o,i){"use strict";o(function(){function e(){window.history.back()}
// if this file has loaded, we want to append an option to let the page know JS is working
// function showHideMore(){
// 	if(!$('.js__showmore').hasClass('active')){
// 		$('.js__showmore').addClass('active');
// 		$('.nu__filters > div > ul > li.inshowmore').each(function(){
// 			$(this).show();
// 		});
// 	}else{
// 		$('.js__showmore').removeClass('active');
// 		$('.nu__filters > div > ul > li.inshowmore').each(function(){
// 			$(this).hide();
// 		});
// 	}
// }
function i(){o(".js__showmore").hasClass("active")||(o(".js__showmore").addClass("active"),o(".nu__filters > div > ul > li.inshowmore").each(function(){o(this).css({opacity:"1.0"})}),o(".nu__filters").css({overflow:"visible"}))}function t(){o(".js__showmore").hasClass("active")&&(o(".js__showmore").removeClass("active"),o(".nu__filters > div > ul > li.inshowmore").each(function(){o(this).css({opacity:"0.0"})}),o(".nu__filters").css({overflow:"hidden"}))}
// function closeMore(){
// 	$('.nu__filters > div > ul > li.inshowmore').each(function(){
// 		$(this).hide();
// 	});
// }
// this function will check filter navs used on pages to see if the items exceed the width of the container
function s(){if(o(window).width()<=620)// we are on a much smaller screen, so ignore the more option and stack via CSS
// console.log('screen size less than 620px');
o(".nu__filters > div > div").hide(),o(".nu__filters > div > ul > li").removeAttr("style"),o(".nu__filters > div > ul > li").removeClass("inshowmore"),exceedsContainer=!1;else{// we need to use the more option to allow user to see all options
// $('.nu__filters > div > div').show();	// we need to show the more option
var e=0,i=o(".nu__filters > div > ul").width(),t=0,s=o(".nu__filters > div > ul > li").first().position().top,n=o(".nu__filters").height()-2;
// console.log(vOffset);
// $('.nu__filters > div > ul > li.inshowmore').removeAttr('style');
// $('.nu__filters > div > ul > li.inshowmore').removeClass('inshowmore');
// $('.nu__filters > div > ul > li.inshowmore').removeAttr('style');
o(".nu__filters > div > ul > li.inshowmore").removeClass("inshowmore"),o(".nu__filters > div > ul > li > a").each(function(e){t+=o(this).outerWidth(),
//console.log($(this).parent().position().top);
// if($(this).parent().position().top > tPos){
i<t?(
//console.log($(this));
o(this).parent().addClass("inshowmore").css({top:n}),n+=o(this).parent().height()):(
// need to re-check the position vs width here to remove styles if no longer hiding
// $('.nu__filters > div > ul > li.inshowmore').removeAttr('style');
// $('.nu__filters > div > ul > li.inshowmore').removeClass('inshowmore');
// // $(this).parent().removeClass('inshowmore').css({'top':vOffset});
// vOffset -= $(this).parent().height();
o(this).parent().removeAttr("style"),o(this).parent().removeClass("inshowmore"))}),
// now let's figure out if the content fits inside the container or not
i<=t+0?exceedsContainer||(
// console.log('content exceeds container!');
exceedsContainer=!0,
// let's show the more button as the items do not fit
o(".nu__filters > div > div").show()):t+0<i&&exceedsContainer&&(
// console.log('content fits within container again!');
exceedsContainer=!1,
// more than enough room, hide the more button
o(".nu__filters > div > div").hide(),o(".nu__filters > div > ul > li.inshowmore").removeAttr("style"),o(".nu__filters > div > ul > li.inshowmore").removeClass("inshowmore"))}}
// if we are NOT on the homepage, kick off a filter check right away
// this is for testing and validating break points based on screen size, turned on and off using global var above
if(
// call the page setup scripts to optimize some items
getWindowSize(),showSize){var n=o(window).width();o("p.testp").text("Screen width is currently: "+n+"px."),o(window).resize(function(){var e=o(window).width();e<=576?o("p.testp").text("Screen width is less than or equal to 576px. Width is currently: "+e+"px."):e<=680?o("p.testp").text("Screen width is between 577px and 680px. Width is currently: "+e+"px."):e<=1024?o("p.testp").text("Screen width is between 681px and 1024px. Width is currently: "+e+"px."):e<=1500?o("p.testp").text("Screen width is between 1025px and 1499px. Width is currently: "+e+"px."):o("p.testp").text("Screen width is greater than 1500px. Width is currently: "+e+"px.")})}
/* ********************************************** */
/* ***************************************************************************
		The following is a set of tools and features for all pages as soon as they
		are loaded
		*************************************************************************** */
// let's check to see if they have accepted the cookie window or not, and display it if they have not accepted
"true"!=localStorage.getItem("acceptCookies")&&o(".cookiewarning").delay(1e3).fadeIn(250),
// listen for the user to click on the accept and continue button for the cookies
o(".cookiewarning").on("click",".js__cookie-accept",function(e){// the user has clicked on the accept button
localStorage.setItem("acceptCookies","true"),// set the cookie into localstorage that they agreed
o(".cookiewarning").fadeOut(250)}),console.log(o("header").outerHeight()),
// we need to set the main content offset based on: utility nav height, alerts height, and main header height
o(".main").css({"margin-top":o("header").outerHeight()}),o("body").addClass("nu-js"),
//needed a way to go back a page if someone clicked the search button from the results page.
//$('input:checkbox').prop('checked', false);
o("nav input").prop("checked",!1),o("body.search .js-search-close").on("click touchend",function(){e()}),
// listen for the user to focus on the search bar so that we can make some small design tweaks if JS is available
0<parseInt(o("form#nu__searchbar-form > div > label").css("left"))&&(// not already small
o("#nu__searchbar").on("focus","form#nu__searchbar-form > div > input",function(e){// focus
o("form#nu__searchbar-form > div > label").addClass("focus");
// $('form#nu__searchbar-form > div > button.reset').css({'color':'rgba(255,255,255,1.0)','pointer-events':'auto'});
}),o("#nu__searchbar").on("blur","form#nu__searchbar-form > div > input",function(e){// blur
""==o(this).val()&&o("form#nu__searchbar-form > div > label").removeClass("focus")})),
// need a listener on the search reset button to cover some other misc. functionality
o("form#nu__searchbar-form").on("click","div > button[type=reset]",function(e){o("form#nu__searchbar-form > div > input").val(""),o("form#nu__searchbar-form > div > input").attr("value",""),o("form#nu__searchbar-form > div > label").removeClass("focus")}),
/* ************************************************************************ */
// /* ***************************************************************************
// The following is a set of tools and features for the homepage
// *************************************************************************** */
// these are stored within the separate homepage.js file
// /* end of the stuff for the homepage */
/* ***************************************************************************
		The following is a set of tools and features for the main navigation
		*************************************************************************** */
// these are stored within the separate mainmenu.js file
/* end of the stuff for the main navigation */
// this will handle clicking on the more button in filter options
o(".nu__filters").on("click",".js__showmore",function(e){
// console.log('I would like to see more!');
// showHideMore();
// $('.nu__filters ul').height('auto');
o(this).hasClass("active")?t():i()}),!o("body").hasClass("home")&&0<o(".nu__filters").length&&s(),
// let's listen for the page to resize and handle some events
o(window).on("resize",function(){getWindowSize(),// check the window size
// if we are NOT on the homepage, kick off a filter check
!o("body").hasClass("home")&&0<o(".nu__filters").length&&(s(),// check to see what needs to be shown and what is overflow for filters
t()),
// console.log($("header").outerHeight());
//
// reset the offset to position content just below the header
o(".main").css({"margin-top":o("header").outerHeight()})}),
// need to set up an on-scroll event that IS NOT going to activate on the homepage
o(window).on("scroll",function(){
// we will ONLY check scroll on pages other than the homepage
o("body").hasClass("home")||
// we should make sure that the more options for filters close if we scroll the page
t()})})}(this,jQuery);