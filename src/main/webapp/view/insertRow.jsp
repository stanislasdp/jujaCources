<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form:form action="insertRow" method="post" modelAttribute="table">
    <h1>${table.name}</h1>
    <table>
        <c:forEach begin="1" end="${table.columnsAmount}" varStatus="el">
            <tr>
                <td>${column}</td>
                <td>
                    <form:input type="text" name="${column}" path="row[${el.index -1}]"/>
                </td>
            </tr>
        </c:forEach>
    </table>
    <input type="submit" name="Insert">
</form:form>
<%@include file="footer.jsp"%>
</body>
</html>
