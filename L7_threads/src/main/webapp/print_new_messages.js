function handleData(data) { // TODO: add error handling
    printMessages(data.messages)
}

function printMessages(messages, printPending) {
    if (printPending === undefined) {
        printPending = false;
    }

    var table = document.getElementById('table');

    for (var i = 0; i < messages.length; ++i) {
        var message = messages[i];

        table.rows[1].remove();

        var tr = document.createElement('tr');

        table.appendChild(tr);

        var username_td = document.createElement('td');
        var text_td = document.createElement('td');
        var timestamp_td = document.createElement('td');

        username_td.innerHTML = message.username;
        text_td.innerHTML = message.text;

        var date = new Date(message.timestamp);

        timestamp_td.innerHTML = date.getDate() + '.' + date.getMonth() + '.' + date.getFullYear() + ' ' +
            date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();

        tr.appendChild(username_td);
        tr.appendChild(text_td);

        if (printPending) {
            var pendingMessage = document.createElement('td');
            pendingMessage.innerHTML = 'Sending...';

            tr.setAttribute('class', 'pending');

            tr.appendChild(pendingMessage);
        } else {
            tr.appendChild(timestamp_td);
        }
    }
}