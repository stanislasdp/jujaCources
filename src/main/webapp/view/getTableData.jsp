<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<spring:url value="${toUrl}" var="url"/>
<form:form action="${url}" method="POST" modelAttribute="table">
    <form:input type="text" path="name" title="Table Name"/>
    <form:input type="text" path="columnsAmount"/>
    <form:errors path = "columnsAmount"/>
    <input type="submit"/>
</form:form>
</body>
</html>
