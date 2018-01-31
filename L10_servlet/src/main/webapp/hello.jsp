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

<h2>${msg}</h2>

<table border="1">
    <tr>
        <td>Name:</td>
        <td>Age:</td>
    </tr>
    <c:forEach items="${allUsers}" var="user">
        <tr>
            <td>${user.login}</td>
            <td>${user.pass}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
