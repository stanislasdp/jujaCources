<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
    <head>
        <title>SQLCmd</title>
    </head>
    <body>
    <spring:url value="/authenticateUser" var="url"/>
        <form:form  method="POST" action="${url}">
            <c:if test="${param.error != null}">
                <i>You have entered invalid login/password</i>
            </c:if>
            <label>User
                <input type="text" name="username">
            </label>
            <label>Password
                <input type="password" name="password">
            </label>
            <input type="submit">
        </form:form>
    </body>
</html>