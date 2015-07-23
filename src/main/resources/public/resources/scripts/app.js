(function($) {
  window.App = {
    fillSubscriptions: function() {
      var source = $('#subscriptionsTemplate').html();
      var template = Handlebars.compile(source);
      $.ajax({
        url: '/subscriptions.json',
        success: function(res) {
          var html = template(res);
          $('#subscriptions').html(html);
        },
        dataType: 'json'
      });
    }
  };
})(jQuery);
