package JSONSingleton;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//todo: JSON singletons are being replaced with single SQL Lite database file
public class FeedbackJSONSingleton {
    //private and static, so it can only be accessed via getInstance()
    private static JSONObject feedbackJSONObject;
    private final static String FEEDBACK_FILE_PATH = "src/main/resources/feedback.json";

    //private constructor so class can only be accessed statically
    private FeedbackJSONSingleton() {}

    public static boolean appendToJSON(JSONObject jsonObject) {
        Boolean successState = false;

        initializeIfSingletonIsNull();


        JSONArray quotes = feedbackJSONObject.getJSONArray("feedback");
        quotes.put(jsonObject);

        synchronized (FeedbackJSONSingleton.class) {
            try (FileWriter file = new FileWriter(FEEDBACK_FILE_PATH)) {
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
            synchronized (FeedbackJSONSingleton.class) {

                //if null, then instantiate
                if (feedbackJSONObject == null) {

                    File file = new File(FEEDBACK_FILE_PATH);
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
