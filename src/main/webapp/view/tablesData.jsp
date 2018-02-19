<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<table border="1">
    <c:forEach items="${tables}" var="table">
        <tr>
            <spring:url value="/tableData/${table}" var="tableUrl"/>
            <td><a href="${tableUrl}">${table}</a></td>
            <spring:url value="/insertToTable/${table}" var="insertUrl"/>
            <td><input type="button" onclick="location.href='${insertUrl}'">Insert</td>
        </tr>
    </c:forEach>
</table>
<%@include file="footer.jsp" %>
</body>
</html>
