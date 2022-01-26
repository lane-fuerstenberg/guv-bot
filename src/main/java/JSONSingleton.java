import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JSONSingleton {

    //private and static, so it can only be accessed via getInstance()
    private static JSONObject JSONSingleton;
    private final static String FILE_PATH = "src/main/resources/database.json";

    //private constructor so class can only be accessed statically
    private JSONSingleton() {}

    public static JSONObject getInstance() {
        initializeIfSingletonIsNull();
        return JSONSingleton;
    }


    public static boolean appendToJSON(JSONObject jsonObject, String arrayName) {
        Boolean successState = false;

        initializeIfSingletonIsNull();


        JSONArray quotes = JSONSingleton.getJSONArray(arrayName);
        quotes.put(jsonObject);

        synchronized (JSONSingleton.class) {
        try (FileWriter file = new FileWriter(FILE_PATH)) {
                file.write(JSONSingleton.toString());
                successState = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return successState;
    }

    private static void initializeIfSingletonIsNull() {
        if (JSONSingleton == null) {
            //delays synchronization only when jsonObject is null for performance reasons
            synchronized (JSONSingleton.class) {
                //if null, then instantiate
                if (JSONSingleton == null) {

                    File file = new File(FILE_PATH);
                    String json = null;

                    try {
                        json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    JSONSingleton = new JSONObject(json);
                }
            }
        }
    }
}
