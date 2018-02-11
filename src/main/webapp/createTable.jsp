<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>SQLCmd</title>
    </head>
    <body>
        <form action="createTable" method="post">
            <table>
                <tr>
                    <td>Table name</td>
                    <td><input type="text" name="tableName"/></td>
                </tr>
                <tr>
                    <td>Column1</td>
                     <td><input type="text" name="Column1"/></td>
                 </tr>
                  <tr>
                      <td>Column1</td>
                      <td><input type="text" name="Column2"/></td>
                  </tr>
                   <tr>
                      <td>Column3</td>
                       <td><input type="text" name="Column3"/></td>
                   </tr>
                   <tr>
                     <td>Column4</td>
                     <td><input type="text" name="Column4"/></td>
                   </tr>
                </tr>
            </table>
            <form> <input type ="submit" value = "create"></form>
        </form>
    </body>
</html>