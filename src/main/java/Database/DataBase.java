package Database;

import javax.sql.rowset.CachedRowSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase {
    private static DataBase dataBase = null;
    final Path currentDir = Paths.get(System.getProperty("user.dir"));
    final Path filePath = Paths.get(currentDir.toString(), "src\\main\\resources", "main.db");
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

    public class Quote{
        public String Name;
        public String Content;

        public Quote(String name, String content){
            Name = name;
            Content = content;
        }
    }

    private Connection connect(){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + filePath);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return c;
    }
    //TODO: Set up API
    private DataBase(){
        connect();
        System.out.println("Opened database successfully");
    }

    //TODO: Get Entry --UID, content, name
    //Cannot return ResultSet as the connection is closed. TODO: Find alternative
    public HashMap<Long, ArrayList<Quote>> GetQuotes(GetByType getByType, String value) throws SQLException {
        HashMap<Long, ArrayList<Quote>> quoteMap = new HashMap<Long, ArrayList<Quote>>();
        try(Connection c = this.connect()) {
            switch (getByType) {
                case UID -> {
                    long uid = Long.parseLong(value);
                    String query = String.format("SELECT * FROM Quotes WHERE Author = %d;", uid);
                    try{
                        Statement statement = c.createStatement();
                        ResultSet results = statement.executeQuery(query);
                        while(results.next()){
                            long resultUID = results.getLong("Author");
                            String name = results.getString("Name");
                            String content = results.getString("Content");
                            if(quoteMap.containsKey(resultUID)){
                                quoteMap.get(resultUID).add(new Quote(name, content));
                            }
                            else{
                                ArrayList<Quote> tempList = new ArrayList<>();
                                tempList.add(new Quote(name, content));
                                quoteMap.put(resultUID, tempList);
                            }
                        }
                        statement.close();
                        return quoteMap;
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case Content -> {
                    String query = "SELECT * FROM Quotes WHERE Content LIKE '%?%';";
                    try(PreparedStatement statement = c.prepareStatement(query)) {
                        statement.setString(1, value);
                        ResultSet results = statement.executeQuery(query);
                        while(results.next()){
                            long resultUID = results.getLong("Author");
                            String name = results.getString("Name");
                            String content = results.getString("Content");
                            if(quoteMap.containsKey(resultUID)){
                                quoteMap.get(resultUID).add(new Quote(name, content));
                            }
                            else{
                                ArrayList<Quote> tempList = new ArrayList<>();
                                tempList.add(new Quote(name, content));
                                quoteMap.put(resultUID, tempList);
                            }
                        }
                        return quoteMap;
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case Name -> {
                    String query = "SELECT * FROM Quotes WHERE Name LIKE '%?%';";
                    try(PreparedStatement statement = c.prepareStatement(query)) {
                        statement.setString(1, value);
                        ResultSet results = statement.executeQuery(query);
                        while(results.next()){
                            long resultUID = results.getLong("Author");
                            String name = results.getString("Name");
                            String content = results.getString("Content");
                            if(quoteMap.containsKey(resultUID)){
                                quoteMap.get(resultUID).add(new Quote(name, content));
                            }
                            else{
                                ArrayList<Quote> tempList = new ArrayList<>();
                                tempList.add(new Quote(name, content));
                                quoteMap.put(resultUID, tempList);
                            }
                        }
                        return quoteMap;
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    //TODO: Create Entry -- UID, name (Character limit?), content (Character limit?)
    public boolean CreateEntry(long UID, String name, String content) throws SQLException {
        //String query = String.format("INSERT INTO Quotes(Name, Author, Content) VALUES('%s', %d, '%s' );", name, UID, content);
        String query = "INSERT INTO Quotes(Author, Name, Content) VALUES(?, ?, ?);";
        try(Connection c = this.connect();
            PreparedStatement preparedStatement = c.prepareStatement(query)) {
            preparedStatement.setLong(1, UID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, content);
            preparedStatement.execute();
            return true;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    //TODO: Delete Entry -- UID, name
    public Boolean RemoveEntry(GetByType getByType, String value) throws SQLException{
        try(Connection c = this.connect()) {
            switch (getByType) {
                case UID -> {
                    long uid = Long.parseLong(value);
                    String query = String.format("DELETE FROM Quotes WHERE Author = %d;", uid);
                    try {
                        Statement statement = c.createStatement();
                        statement.executeUpdate(query);
                        return true;
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case Content -> {
                    String query = String.format("DELETE FROM Quotes WHERE Content = '%s';", value);
                    try {
                        Statement statement = c.createStatement();
                        statement.executeUpdate(query);
                        return true;
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case Name -> {
                    String query = String.format("DELETE FROM Quotes WHERE Name LIKE '%s';", value);
                    try {
                        Statement statement = c.createStatement();
                        statement.executeUpdate(query);
                        return true;
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        return false;
    }

    //TODO: Update Entry?
}
