<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title>SQLCmd</title>
</head>
<body>
<spring:url value="/createTable" var="url"/>
<form:form action="${url}" method="post" modelAttribute="table">
    <table>
        <tr>
            <td>Table name is ${table.name}</td>
        </tr>
        <form:hidden path="name" value="${table.name}"/>
        <form:hidden path="columnsAmount" value="${table.columnsAmount}"/>
        <c:forEach begin="1" end="${table.columnsAmount}" varStatus="el">
            <tr>
                <td>
                    <form:input type="text" path="columns[${el.index -1}]"/>
                </td>
            </tr>
        </c:forEach>
    </table>
    <form><input type="submit"></form>
</form:form>
</body>
</html>