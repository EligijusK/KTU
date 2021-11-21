document.body.className += ' js-enabled';

$(function() {
  /*  Search button expando  */
  $('.toplevel-search button').css('width', 27);
  $('.toplevel-search input').focus(function() {
    $(this).siblings('button').animate({ width: 85 }, 200);
  }).blur(function(e) {
    $(this).siblings('button').animate({ width: 27 }, 200);
  });
  $('.toplevel-search button').mousedown(function() {
    var _this = $(this);
    setTimeout(function() { _this.stop().css({ width: 85 }, { queue: false, duration: 200 }); }, 1);
  });

  /*  Homepage Banners  */
  var homeTimer = false;
  // Hide all banners except the first one
  $('.home-banner').hide().last().show().addClass('current');

  // Only add the banner navigation if there is more than one banner
  if ($('#home-banners .home-banner').length > 1) {
    // Start the banners in a paused state
    $('#home-banners').addClass('paused');
    // Inject the banner navigation
    $('#home-banners').append('<p class="banner-navigation"><a class="previous-banner" href="#">Previous</a><a class="play-pause-banner" href="#">Play/Pause</a><a class="next-banner" href="#">Next</a><span class="banner-count"></span></p>');
    // Initialise banner count
    $('.banner-navigation .banner-count').html('<em>1</em>/'+$('#home-banners .home-banner').length)
    // Update banner count
    var updateBannerCount = function(target) {
      var target = target || '#home-banners .home-banner:visible';
      var count = $(target).index('#home-banners .home-banner');
      if (count > $('#home-banners .home-banner').length) count = 0;
      $('.banner-navigation .banner-count em').html(count + 1);
    }
    // Delegate click events
    $('#home-banners').delegate('.next-banner', 'click', function(e) {
      e.preventDefault();
      clearTimeout(homeTimer);
      var callback = function() {};
      // If the click originates from a real click event (assertained by checking for e.screenX), then pause the banner
      if (e.screenX && !$('#home-banners').is('.paused')) {
        $('#home-banners .play-pause-banner').click();
      } else if (!e.screenX && !$('#home-banners').is('.paused')) {
        // If the click doesn't originate from a real click event, and the slideshow isn't paused, queue up the next slide
        callback = function() { homeTimer = setTimeout(function() { $('#home-banners .next-banner').click(); }, 5000); }
      }
      // Find the currently visible banner
      var current = $('#home-banners .current'),
          next = current.next('.home-banner');
      // If there is no next banner, cycle back to the start
      if (!next.length) next = $('#home-banners .home-banner').first();
      // Fade out the current banner and fade in the new one
      current.removeClass('current').fadeOut(750);
      next.addClass('current').fadeIn(750, callback);
      // Update banner count
      updateBannerCount(next);
    });
    $('#home-banners').delegate('.previous-banner', 'click', function(e) {
      e.preventDefault();
      clearTimeout(homeTimer);
      // If the click originates from a real click event (ascertained by checking for e.screenX), then pause the banner
      if (e.screenX && !$('#home-banners').is('.paused')) $('#home-banners .play-pause-banner').click();
      // Find the currently visible banner
      var current = $('#home-banners .current'),
          prev = current.prev('.home-banner');
      // If there is no previous banner, cycle back to the last one
      if (!prev.length) prev = $('#home-banners .home-banner').last();
      // Fade out the current banner and fade in the new one
      current.removeClass('current').fadeOut(750);
      prev.addClass('current').fadeIn(750);
      // Update banner count
      updateBannerCount(prev);
    });
    $('#home-banners').delegate('.play-pause-banner', 'click', function(e) {
      e.preventDefault();
      if ($('#home-banners').is('.paused')) {
        // Play the homepage banners
        $('#home-banners').removeClass('paused');
        $('#home-banners .next-banner').click();
      } else {
        // Pause the homepage banners
        clearTimeout(homeTimer);
        $('#home-banners').addClass('paused');
      }
    });

    $('#home-banners').focusin(function(e) {
      // Pause the homepage banners
      clearTimeout(homeTimer);
      $(this).addClass('paused');
    });

    // Autoplay the banners
    $('#home-banners .play-pause-banner').click();
  }

  // Hide the topnav when the search is open
  // (because it has a higher z-index than the search box)
  $('#g-global-search-overlay #q').focus(function() {
    $('.toplevel-nav').hide();
  }).blur(function() {
    $('.toplevel-nav').show();
  });

});