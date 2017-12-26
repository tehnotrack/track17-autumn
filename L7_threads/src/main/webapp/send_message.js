function printPendingMessage(messages) {
    printMessages([messages], true);
}

function print(data) {
    var pendingMessages = document.getElementsByClassName('pending');

    Array.prototype.forEach.call(pendingMessages, function (message) {

        setTimeout(function () {
            message.remove();
        }, 1000);

        message.lastChild.innerHTML = 'Successfully sent!';
    });
}

$(document).on('click', '#send', function (e) {
    e.preventDefault();

    var data = {
        'username': $("input[name='username']").val(),
        'text': $("input[name='text']").val()
    };

    printPendingMessage(data);

    $.ajax(
        {
            url: '/send',
            method: 'POST',
            data: data,
            async: true
        }
    )
        .done(function (data, status, response) {
            print(data);
        });
});