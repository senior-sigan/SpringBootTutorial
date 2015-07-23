(function($) {
  window.App = {
    fillSubscriptions: function() {
      var source = $('#subscriptionsTemplate').html();
      var template = Handlebars.compile(source);
      $.ajax({
        url: '/subscriptions.json',
        success: function(res) {
          var html = template(res);
          $('#js-subscriptions').html(html);
        },
        dataType: 'json'
      });
    },
    
    deleteSubscription: function(event) {
      event.preventDefault();
      var id = this.parentElement.parentElement.dataset.id;
      var token = $("meta[name='_csrf']").attr("content");
      var header = $("meta[name='_csrf_header']").attr("content");
      var headers = {};
      headers[header] = token;
      
      $.ajax({
        url: '/subscriptions/' + id + '.json',
        dataType: 'json',
        headers: headers,
        method: 'DELETE',
        success: function(res) {
          $('tr[data-id=' + id +']').remove();
        }
      });
    }
  };
  
  $(function(){
    var $subscriptions = $('#js-subscriptions');
    
    if ($subscriptions.length) {
      App.fillSubscriptions();
      $subscriptions.on('click', '.js-remove', App.deleteSubscription);
    }     
  });
})(jQuery);
