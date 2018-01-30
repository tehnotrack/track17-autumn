<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>User-History</title>
</head>
<body>

<h2>User-history</h2>

<table border="1">
    <tr>
        <td>Username</td>
        <td>Text</td>
        <td>Time</td>
    </tr>
    <c:forEach items="${msgList}" var="msg">
        <tr>
            <td>${msg.ownerLogin}</td>
            <td>${msg.text}</td>
            <td>${msg.ts}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
