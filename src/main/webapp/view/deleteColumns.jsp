<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<spring:url value="/deleteRowsFromTable/${table.name}" var="url"/>
<form:form action="${url}" method="post" modelAttribute="table">
    <h1>${table.name}</h1>
    <table>
        <c:forEach items="${table.columns}" var="column" varStatus="el">
            <tr>
                <td>${column}</td>
                <td>
                    <form:input path="columns[${el.index}]" value="${table.columns[el.index]}"/>
                </td>
                <td>
                    <form:input name="${column}" path="row[${el.index}]"/>
                </td>
            </tr>
        </c:forEach>
        <form:checkboxes path="selectedColumn" items="${table.columns}"/>
    </table>
    <input type="submit">
</form:form>
<%@include file="footer.jsp"%>
</body>
</html>
