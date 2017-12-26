function printNewMessages(data) {
    var table = document.getElementById('table');

    for (var i = 0; i < data.messages.length; ++i) {
        var message = data.messages[i];

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
        tr.appendChild(timestamp_td);
    }
}