<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>SQLCmd</title>
    </head>
    <body>
    Существующие команды:<br>
        connect|databaseName|userName|password<br>
            для подключения к базе данных, с которой будем работать<br>
        tables<br>
            для получения списка всех таблиц базы, к которой подключились<br>
        clear|tableName<br>
            для очистки всей таблицы<br>
        create|tableName|column1|value1|column2|value2|...|columnN|valueN<br>
            для создания записи в таблице<br>
        find|tableName<br>
            для получения содержимого таблицы tableName<br>
        help<br>
            для вывода этого списка на экран<br>
        exit<br>
            для выхода из программы<br>
        You can goto <a href="menu">Menu</a><br>
    </body>
</html>