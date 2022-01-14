package JSONSingleton;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


//todo: JSON singletons are being replaced with single SQL Lite database file
public class QuoteJSONSingleton {
    public static final String QUOTE_FILE_PATH = "src/main/resources/quote.json";
    //private and static, so it can only be accessed via getInstance()
    private static JSONObject feedbackJSONObject;

    //private constructor so class can only be accessed statically
    private QuoteJSONSingleton() {}

    public static JSONObject getInstance() {
        initializeIfSingletonIsNull();

        return feedbackJSONObject;
    }

    public static boolean appendToJSON(JSONObject jsonObject) {
        Boolean successState = false;

        initializeIfSingletonIsNull();


        JSONArray quotes = feedbackJSONObject.getJSONArray("quotes");
        quotes.put(jsonObject);

        synchronized (QuoteJSONSingleton.class) {
            try (FileWriter file = new FileWriter(QUOTE_FILE_PATH)) {
                file.write(feedbackJSONObject.toString());
                successState = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return successState;
    }

    private static void initializeIfSingletonIsNull() {
        if (feedbackJSONObject == null) {
            //delays synchronization only when jsonObject is null for performance reasons
            synchronized (QuoteJSONSingleton.class) {

                //if null, then instantiate
                if (feedbackJSONObject == null) {

                    File file = new File(QUOTE_FILE_PATH);
                    String json = null;

                    try {
                        json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    feedbackJSONObject = new JSONObject(json);
                }
            }
        }
    }
}
