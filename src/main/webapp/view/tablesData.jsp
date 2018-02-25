<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8"%>
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
            <spring:url value="/updateTable/${table}" var="updateUrl"/>
            <td><input type="button" onclick="location.href='${updateUrl}'">Update</td>
            <spring:url value="/deleteFromTable/${table}" var="deleteUrl"/>
            <td><input type="button" onclick="location.href='${deleteUrl}'">Delete</td>
            <spring:url value="/dropTable/${table}" var="dropUrl"/>
            <td><input type="button" onclick="location.href='${dropUrl}'">Drop</td>
        </tr>
    </c:forEach>
</table>
<%@include file="footer.jsp" %>
</body>
</html>
