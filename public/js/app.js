$('input#create').on('mousedown',function() {
  var openId = $('input#create-openId').val();

  if (!openId) return;

  var options = {
    url: '/user',
    method: 'POST',
    data: {
      "openId" : openId
    },
    success: function(data,status,xhr) {
      console.log('created successfully');

      if (data.created)
        select(data.created);
    },
    error: function(xhr,status,ex) {
      console.log('created unsuccessfully');
      console.log('status: ' + status);
      console.log('ex: ' + ex);
    }
  };

  $.ajax(options);

});

function select(oid) {

  if (!oid) {
    $('#user-selected').hide();
    $('#no-user-selected').show();
    return;
  }
  
  $('.search-results').hide();

  var options = {
    url: '/user/' + oid,
    method: 'GET',
    success: function(data,status,xhr) {
      console.log('got user');

      $('#user-id').html(oid);
      $('#user-openId').html(data.openId);
      $('#user-created').html(new Date(data.insertTime));

      $('#no-user-selected').hide();
      $('#user-selected').show();
    },
    error: function(xhr,status,ex) {
      console.log('no user found');
      $('#user-selected').hide();
      $('#no-user-selected').show();
    }
  };

  $.ajax(options);
};

$('input#search-openId').on('keydown',function() {

  setTimeout(function() {

    $('.search-results').html('');

    $('.search-results').hide();

    var openId = $('input#search-openId').val();

    if (!openId) return;

    var options = {
      url: '/user/find/' + openId,
      method: 'GET',
      success: function(data,status,xhr) {
        console.log('searched successfully');

        var n = data.length;

        $('.search-results').show();

        for (var i = 0; i < n; i++) {
          $('.search-results').append("<div class='search-result' onclick='select(\"" + data[i].oid + "\");'>" + data[i].openId + "</div>");
        }
      },
      error: function(xhr,status,ex) {
        console.log('searched unsuccessfully');
        console.log('status: ' + status);
        console.log('ex: ' + ex);
      }
    };

    $.ajax(options);
  },100);
});
