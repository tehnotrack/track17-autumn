<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Title</title>

    <script type="text/javascript" src="jquery.js"></script>
    <script type="text/javascript" src="send_message.js"></script>
    <script type="text/javascript" src="get_new_messages.js"></script>
    <script type="text/javascript" src="print_new_messages.js"></script>

</head>
<body>

<h1>Messages</h1>

<form action="" method="get">
    The amount of messages in one page: <input type="number" value="10" name="limit">
    <input type="submit" value="Change">
</form>

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