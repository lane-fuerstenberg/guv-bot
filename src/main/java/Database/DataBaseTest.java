package Database;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseTest {

    @Test
    public void GetEntryByUIDTest() throws SQLException {
        String expected = "aile";
        long uid = 248812560923623425L;
        HashMap<Long, ArrayList<DataBase.Quote>> quotes = DataBase.getInstance().GetQuotes(DataBase.GetByType.UID, String.valueOf(uid));
        String actual = quotes.get(uid).get(0).Name;
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void GetEntryByNameTest() throws SQLException {
        String expected = "aile";
        long uid = 248812560923623425L;
        HashMap<Long, ArrayList<DataBase.Quote>> quotes = DataBase.getInstance().GetQuotes(DataBase.GetByType.Name, "aile");
        String actual = quotes.get(uid).get(0).Name;
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void GetEntryByContentTest() throws SQLException {
        String expected = "aile";
        long uid = 248812560923623425L;
        HashMap<Long, ArrayList<DataBase.Quote>> quotes = DataBase.getInstance().GetQuotes(DataBase.GetByType.Content, "aile");
        String actual = quotes.get(uid).get(0).Name;
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void CreateEntryTest() throws SQLException {
        long uid = 248812560923623425L;
        boolean results = DataBase.getInstance().CreateEntry(uid, "test", "test");
        Assert.assertTrue(results);
    }

    @Test
    public void RemoveEntryByNameTest() throws SQLException{
        String expected = "test";
        long uid = 248812560923623425L;
        HashMap<Long, ArrayList<DataBase.Quote>> quotes = DataBase.getInstance().GetQuotes(DataBase.GetByType.Name, "test");
        if(quotes.isEmpty()){
            CreateEntryTest();
            RemoveEntryByNameTest();
        }
        boolean result = DataBase.getInstance().RemoveEntry(DataBase.GetByType.Name, "test");

        Assert.assertTrue(result);
    }

}
