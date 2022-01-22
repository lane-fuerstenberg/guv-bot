package Database;

import javax.xml.crypto.Data;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class DataBase {
    private static DataBase dataBase = null;
    final Path currentDir = Paths.get(System.getProperty("user.dir"));
    final Path filePath = Paths.get(currentDir.toString(), "src\\main\\resources", "main.db");
    private  Connection c = null;
    public enum GetByType {
        UID,
        Content,
        Name
    }

    public static DataBase getInstance(){
        if(dataBase == null)
            dataBase = new DataBase();
        return dataBase;
    }
    //TODO: Set up API
    private DataBase(){
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + filePath.toString());
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    //TODO: Get Entry --UID, content, name
    //Should return ResultSet
    public ResultSet GetQuotes(GetByType getByType, String value){
        if(c == null) return null;
        switch (getByType){
            case UID -> {
                String query = String.format("SELECT * FROM Quotes WHERE Author == %d;", Integer.parseInt(value));
                try {
                    Statement statement = c.createStatement();
                    return statement.executeQuery(query);
                }
                catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }
            case Content -> {
                String query = String.format("SELECT * FROM Quotes WHERE Content LIKE '%%%s%%';", value);
                try {
                    Statement statement = c.createStatement();
                    return statement.executeQuery(query);
                }
                catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }
            case Name -> {
                String query = String.format("SELECT * FROM Quotes WHERE Name LIKE '%%%s%%';", value);
                try {
                    Statement statement = c.createStatement();
                    return statement.executeQuery(query);
                }
                catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }
        }
        return null;
    }

    //TODO: Create Entry -- UID, name, content

    //TODO: Delete Entry -- UID, name

    //TODO: Update Entry?
}
