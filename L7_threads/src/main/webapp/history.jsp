<%--
  Created by IntelliJ IDEA.
  track.servlet.container.User: dmirty
  Date: 04/12/16
  Time: 19:09
  To change this template use File | Settings | File Templates.
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
    <table border="1">
        <tr>
            <td>Name:</td>
            <td>Age:</td>
            <td>Age:</td>
        </tr>
        <c:forEach items="${messages}" var="message">
            <tr>
                <td>${message.getUsername()}</td>
                <td>${message.getText()}</td>
                <td>${message.getFormattedTimestamp()}</td>
            </tr>
        </c:forEach>
    </table>

    <form action="/send" method="post">

        <p><label for="username">Логин: </label><input id="username" type="username" name="username"></p>
        <p><label for="text">Текст: </label><input id="text" type="text" name="text"></p>

        <input type="submit" value="Send message">

    </form>
</body>
</html>