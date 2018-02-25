<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
    <head>
        <title>SQLCmd</title>
    </head>
    <body>
    <spring:url value="/connect" var="fromUrl"/>
        <form:form action="${fromUrl}" method="POST" modelAttribute="connect">
            <table>
                <tr>
                    <td>Database3 name</td>
                    <td><form:input path="database" id="dbname" /></td>
                </tr>
                <tr>
                    <td>User name</td>
                    <td><form:input path="user" id="username"/></td>
                </tr>
                <tr>
                    <td>Password</td>
                    <td><form:input path="password" id="password"/></td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit"></td>
                </tr>
            </table>
        </form:form>
    </body>
</html>