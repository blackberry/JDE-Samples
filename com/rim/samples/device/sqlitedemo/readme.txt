The following is the java code that was used to create the tables in the pre-configured database included
with the SQLiteDemo project.


try
{     
    //Open or create the database
    Database db = DatabaseFactory.openOrCreate("SQLiteDemoDirectory");
            
    // Create the Category table if it does not already exist 
    Statement statement = db.createStatement("CREATE TABLE IF NOT EXISTS Category(category_id INTEGER primary key, category_name TEXT)"); 
    statement.prepare();
    statement.execute();
    statement.close();            
            
    // Create the DirectoryItems table if it does not already exist
    statement = db.createStatement("CREATE TABLE IF NOT EXISTS DirectoryItems(id INTEGER PRIMARY KEY, category_id INTEGER, item_name TEXT, location TEXT,         phone TEXT, FOREIGN KEY (category_id) REFERENCES Category(category_id))"); 
    statement.prepare();
    statement.execute();       
    statement.close();         
}
catch(DatabaseException dbe)
{
    System.err.println(dbe.toString());
} 
