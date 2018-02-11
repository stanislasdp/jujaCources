<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: stas
  Date: 21.01.18
  Time: 17:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="insertRow" method="post">
<input type="hidden" name="tableName" value="${tableName}">
    <h1>${tableName}</h1>
    <table>
        <c:forEach items="${columnNames}" var="column">
            <tr>
                <td>${column}</td>
                <td>
                    <input type="text" name="${column}">
                </td>
            </tr>
        </c:forEach>
    </table>
    <input type="submit" name="Insert">
</form>
<%@include file="footer.jsp"%>
</body>
</html>
