UserStories
1. As a user I want to connect to existent DB and perform CRUD operations, so that:
1.1 (connection successful) I see app welcome message > I enter proper database credentials> 
I see that the connection is successful. 
1.2 (wrong DB name) I see app welcome screen > I enter not proper database name > 
 I see the message about wrong connection parameters > I can enter database credentials one more time.
1.3 (wrong user) I see app welcome screen > I enter not proper database name > 
I see the message about wrong connection parameters > I can enter database credentials one more time.
1.4 (wrong password) I see app welcome screen > 
I enter not proper database name > I see the message about wrong connection parameters > I can enter database credentials one more time.
1.5 (some credentials are not specified) I see app welcome screen > I do not provide some database credentials > 
I see an error that some credentials are not present > I can enter credentials one more time.
2. As a user I want to get list of all tables present in DB
2.1 (some tables are present in DB) I enter `list`command > I see all tables present in DB separated by commas.
2.2(no tables are present in DB) I enter `list` command > I see the message that no tables are present.
3. As a user I want to view content of particular table in DB 
3.1 (table with data is present) I enter `find` command with the table name >
 I see all table columns names and data in rows corresponding to that columns
3.2 (table is present but empty) I enter `find` command with the table name >  I see the message that table is empty
3.3 (table is not present) I enter `find` command with not existent table name > I see the message that table does not exist   
4. As a user I want to insert new row to database table
4.1 (new row is inserted to selected table) I enter `insert` command for table with specified columns and values in columns >
I get the message that new row is inserted to database.
4.2 (new row is inserted to not existent table) I enter `insert` command for not existent table with specified columns and values > 
I get the message that the table does snot exist;
4.3 (new row is inserted to existent database, but count of columns and values specified in columns does not correspond) 
I enter `insert` command and invalid values > I get the message that row could not be inserted in database. 
5 As a user I want to update database row (or rows) found by specified column value
5.1 (row is updated in existent table) I enter `update` command with existent table name with specified column and value to find row to update
 and new values > I get all updated rows on the screen
5.2 (row is inserted to non existent table) > I enter `update` command with not existent table with specified column and value to find row to update >
I get the message that the table does not exist.
5.3 (row is inserted to existent table, but no rows matched specified column value) I enter `update` command with specified column and value >
I get the message that no columns has been updated
5.4 (update command is applied to existent table, but column or/and value to find rows 
to update are not specified) > I enter `update`, but there is wrong command syntax >
I get the message that columns are not specified in parameters
6. As I user I want to delete database row(rows) found by specified column value
6.1 (row is deleted from existent table) > I enter `delete` command for existent table with column value 
to find row to delete specified > I see rows that have been deleted
6.2(try to delete row from not existent table) > I enter `delete` command for not existent table > 
I see error message that specified table does not exist 
6.3(try to delete row from existent table, but column value to find row is not specified) > 
I enter `delete` command for existent table, but does not specify column or/and value to delete > 
I get error message about incorrect parameters
6.4(delete command is applied to existent table, but no rows is matched) I enter `delete` command 
for existent table, but no rows is matched in the table > I get the message that no values is present.
7. As a user I want to drop any table in database
7.1 (drop existent table) I enter `drop` command for existent table > I get the message that the table is successfully dropped.
7.2 (drop not existent table) I enter `drop` command for not existent table > I get the message that the table does not exist
8. As a user I want to exit from application) I enter `exit` command > I get the message that connection to database has been closed 
and application terminates.
8.1 As a user I want to see all available commands and their syntax > I enter `exit` command  >
 I get the message about all possible commands 
   
          
