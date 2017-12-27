function getNewMessages() {
    $.ajax(
        {
            url: '/check_new_messages',
            method: 'POST',
            async: true
        }
    ).done(function (data, status, response) {
        handleData(data);
    });
}

$(document).ready(function () {
    setInterval(getNewMessages, 2000);
});