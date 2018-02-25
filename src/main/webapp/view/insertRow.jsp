<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title3</title>
</head>
<body>
<spring:url value="/insertRowToTable" var="url"/>
<form:form action="${url}/${table.name}" method="post" modelAttribute="table">
    <table>
        <h
        <c:forEach items="${table.columns}" var="column" varStatus="el">
            <tr>
                <td><form:input path="columns[${el.index}]" value="${table.columns[el.index]}"/></td>
                <td>
                    <form:input type="text" name="${column}" path="row[${el.index}]"/>
                </td>
            </tr>
        </c:forEach>
    </table>
    <input type="submit">
</form:form>
<%@include file="footer.jsp"%>
</body>
</html>