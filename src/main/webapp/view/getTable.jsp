<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<spring:url value="/${toUrl}" var="url"/>
<form action="${url}" title="Table Name">
    <label>
        <input type="text" id="table">
    </label>
    <input type="submit" value="${url/table}">
</form>
</body>
</html>
