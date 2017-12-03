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
I enter `insert` command and invalid values > I get the message that rowcould not be inserted in database.  
          
