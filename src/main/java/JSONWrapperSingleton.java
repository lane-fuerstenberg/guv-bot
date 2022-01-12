import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;



public class JSONWrapperSingleton {
    //private and static, so it can only be accessed via getInstance()
    private static JSONObject quoteJSONObject;

    //private constructor so class can only be accessed statically
    private JSONWrapperSingleton() {}

    public static JSONObject getInstance() {
        initializeIfSingletonIsNull();

        return quoteJSONObject;
    }

    public static boolean appendToJSON(JSONObject jsonObject) {
        Boolean successState = false;

        initializeIfSingletonIsNull();


        JSONArray quotes = quoteJSONObject.getJSONArray("quotes");
        quotes.put(jsonObject);

        synchronized (JSONWrapperSingleton.class) {
            try (FileWriter file = new FileWriter("src/main/resources/quote.json")) {
                file.write(quoteJSONObject.toString());
                successState = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return successState;
    }

    private static void initializeIfSingletonIsNull() {
        if (quoteJSONObject == null) {
            //delays synchronization only when jsonObject is null for performance reasons
            synchronized (JSONWrapperSingleton.class) {

                //if null, then instantiate
                if (quoteJSONObject == null) {

                    File file = new File("src/main/resources/quote.json");
                    String json = null;

                    try {
                        json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    quoteJSONObject = new JSONObject(json);
                }
            }
        }
    }
}
