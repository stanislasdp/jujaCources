<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>SQLCmd</title>
    </head>
    <body>
        <c:forEach items="${items}" var="item">
            <spring:url value="${item}" var="url"/>
            <a href="${url}">${item}</a><br>
        </c:forEach>
    </body>
</html>