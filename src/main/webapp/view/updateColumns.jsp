<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<spring:url value="/updateRowInTable" var="url"/>
<form:form action="${url}/${table.name}" method="post" modelAttribute="table">
    <h1>${table.name}</h1>
    <table>
        <c:forEach items="${table.columns}" var="column" varStatus="el">
            <tr>
                <td>
                    <form:input path="columns[${el.index}]" value="${table.columns[el.index]}"/>
                </td>
                <td>
                    <label>
                        <form:input name="${column}" path="row[${el.index}]"/>
                    </label>
                </td>
            </tr>
        </c:forEach>
    </table>
    <label>
        <form:checkboxes path="selectedColumn" items="${table.columns}"/>
    </label>Check to update<br>
    <input type="submit">
</form:form>
<%@include file="footer.jsp" %>
</body>
</html>
