$(document).on('click', '#send', function (e) {
    e.preventDefault();

    var data = $("#send_message_form").serializeArray();

    $.ajax(
        {
            url: '/send',
            method: 'POST',
            data: {
                'username': $("input[name='username']").val(),
                'text': $("input[name='text']").val()
            },
            async: true
        }
    ).done(function (data, status, response) {
        printNewMessages(data);
    });
});