function getNewMessages() {
    $.ajax(
        {
            url: '/check_new_messages',
            method: 'POST',
            async: true
        }
    ).done(function (data, status, response) {
        console.log("server replied");
        printNewMessages(data);
    });
}

$(document).ready(function () {
    setInterval(getNewMessages, 5000);
});