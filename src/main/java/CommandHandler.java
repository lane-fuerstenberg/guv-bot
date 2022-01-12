import JSONSingleton.FeedbackJSONSingleton;
import JSONSingleton.QuoteJSONSingleton;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CommandHandler {
    HashMap<Long, Command> commandHashMap;
    final Long QUOTE_ID = 930450163095449601L;
    final Long QUOTE_ADD_ID = 930779269817303050L;
    final Long FEEDBACK_ID = 930812403984265216L;

    interface Command {
        CompletableFuture<InteractionOriginalResponseUpdater> run(SlashCommandInteraction interaction);
    }

    public CommandHandler() {
        commandHashMap = new HashMap<>();
        commandHashMap.put(QUOTE_ID, interaction ->  quoteRetrieve(interaction));
        commandHashMap.put(QUOTE_ADD_ID, interaction -> quoteAdd(interaction));
        commandHashMap.put(FEEDBACK_ID, interaction -> addFeedback(interaction));
        //todo: remove quote command
        //todo: blacklist user command
        //todo: view quotes
    }

    private  CompletableFuture<InteractionOriginalResponseUpdater> addFeedback(SlashCommandInteraction interaction) {
        String input = interaction.getOptionStringValueByName("input").orElse("");
        String userID = interaction.getUser().getIdAsString();
        String date = interaction.getCreationTimestamp().toString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("input", input);
        jsonObject.put("user-id", userID);
        jsonObject.put("date", date);

        if (FeedbackJSONSingleton.appendToJSON(jsonObject)) {
            return interaction.createImmediateResponder().setContent("Your feedback has been received.").respond();
        } else {
            return interaction.createImmediateResponder().setContent("Your quote has **not** been received.").respond();
        }
    }

    private  CompletableFuture<InteractionOriginalResponseUpdater> quoteAdd(SlashCommandInteraction interaction) {
        String name = interaction.getOptionStringValueByName("name").orElse("");
        String content = interaction.getOptionStringValueByName("content").orElse("");
        String userID = interaction.getUser().getIdAsString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("content", content);
        jsonObject.put("user-id", userID);

        if (QuoteJSONSingleton.appendToJSON(jsonObject)) {
            return interaction.createImmediateResponder().setContent("Your quote has been added.").respond();
        } else {
            return interaction.createImmediateResponder().setContent("Your quote has **not** been added.").respond();
        }
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> quoteRetrieve(SlashCommandInteraction interaction) {
        JSONObject jsonObject = QuoteJSONSingleton.getInstance();

        JSONArray quotes = jsonObject.getJSONArray("quotes");
        String quoteName = interaction.getFirstOptionStringValue().orElse("");

        for (int i = 0; i < quotes.length(); i++) {
            JSONObject quoteJSONObject = quotes.getJSONObject(i);

            if (quoteJSONObject.getString("name").equals(quoteName)) {
                String content = quoteJSONObject.getString("content");
                return interaction.createImmediateResponder().setContent(content).respond();
            }
        }

        return interaction.createImmediateResponder().setContent("No matching quotes found.").respond();
    }

    public Command getCommand(long commandID) {
        return commandHashMap.get(commandID);
    }
}
