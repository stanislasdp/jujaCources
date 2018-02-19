<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>SQLCmd</title>
    </head>
    <body>
            <table border="1">
                <c:forEach items="${tableContent}" var="row">
                    <tr>
                        <c:forEach items="${row}" var="element">
                            <td>${element}</td>
                        </c:forEach>
                    </tr>
                </c:forEach>
            </table>
    <%@include file="footer.jsp"%>
    </body>
</html>