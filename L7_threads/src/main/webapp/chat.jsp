<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Title</title>

    <link rel="stylesheet" type="text/css" href="css/jquery.datetimepicker.min.css"/>

    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/send_message.js"></script>
    <script type="text/javascript" src="js/get_new_messages.js"></script>
    <script type="text/javascript" src="js/print_new_messages.js"></script>

    <script src="js/jquery.datetimepicker.full.js"></script>

</head>
<body>

<h1>Messages</h1>

<form action="/history" method="get" id="page_settings">
    <p>The amount of messages in one page: <input type="number" value="10" name="limit"></p>
    <p>filter datetime from: <input type="text" class="datetime" name="from"></p>
    <p>filter datetime to: <input type="text" class="datetime" name="to"></p>
    <p>filter by user: <input type="text" name="user"></p>
    <input type="submit" value="Change">
</form>

<script type="text/javascript">
    $('.datetime').datetimepicker();
</script>

<script type="text/javascript">
    $('#page_settings').submit(function () {
        var fromInput = $('input[name="from"]');
        var toInput = $('input[name="to"]');

        var unixTime = Date.parse(fromInput.val());

        if (!isNaN(unixTime)) {
            fromInput.val(unixTime);
        }

        unixTime = Date.parse(toInput.val());

        if (!isNaN(unixTime)) {
            toInput.val(unixTime);
        }

        return true;
    });
</script>

<div id="errors">
    <c:forEach items="${errors}" var="error">
        <p style="color: red;">${error}</p>
    </c:forEach>
</div>

<table border="1" id="table">
    <tr>
        <td>Username:</td>
        <td>Message:</td>
        <td>Time:</td>
    </tr>
    <c:forEach items="${messages}" var="message">
        <tr>
            <td>${message.getUsername()}</td>
            <td>${message.getText()}</td>
            <td>${message.getFormattedTimestamp()}</td>
        </tr>
    </c:forEach>
</table>

<form action="/send">

    <p><label for="username">Логин: </label><input id="username" type="text" name="username"></p>
    <p><label for="text">Текст: </label><input id="text" type="text" name="text"></p>

    <button id="send">Отправить</button>

</form>

</body>
</html>