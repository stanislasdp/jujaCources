<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="updateRowsTable" method="post">
<input type="hidden" name="tableName" value="${tableName}">
    <h1>${tableName}</h1>
    <table>
        <c:forEach items="${columnNames}" var="column">
            <tr>
                <td>${column}</td>
                <td>
                <input type="checkbox" name="${column}_checkbox" value="${column}_checkbox">Criteria<br>
                </td>
                <td>
                <input type="text" name="${column}">
                </td>
            </tr>
        </c:forEach>
    </table>
    <input type="submit" name="Update">
</form>
<%@include file="footer.jsp"%>
</body>
</html>
