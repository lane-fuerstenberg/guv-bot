package Database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

public class DataBase {

    final Path currentDir = Paths.get(System.getProperty("user.dir"));
    final Path filePath = Paths.get(currentDir.toString(), "src\\main\\resources", "main.db");
    //TODO: Set up API
    public DataBase(){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + filePath.toString());
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    //TODO: Get Entry --all, UID, content, name

    //TODO: Create Entry -- UID, name, content

    //TODO: Delete Entry -- UID, name

    //TODO: Update Entry
}
