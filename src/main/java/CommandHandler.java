import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CommandHandler {
    HashMap<String, Command> commandHashMap;
    final String QUOTE_COMMAND = "quote";
    final String QUOTE_ADD_COMMAND = "quote-add";
    final String FEEDBACK_COMMAND = "feedback";
    final String REMOVE_QUOTE_COMMAND = "remove-quote";
    final String SEARCH_QUOTE_COMMAND = "search-quote";

    interface Command {
        CompletableFuture<InteractionOriginalResponseUpdater> run(SlashCommandInteraction interaction);
    }

    public CommandHandler() {
        commandHashMap = new HashMap<>();
        commandHashMap.put(QUOTE_COMMAND, interaction -> quoteRetrieve(interaction));
        commandHashMap.put(QUOTE_ADD_COMMAND, interaction -> quoteAdd(interaction));
        commandHashMap.put(FEEDBACK_COMMAND, interaction -> addFeedback(interaction));
        commandHashMap.put(REMOVE_QUOTE_COMMAND, interaction -> removeCommand(interaction));
        //commandHashMap.put(SEARCH_QUOTE_COMMAND, interaction -> searchCommand(interaction));
        //todo: view quotes
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> searchCommand(SlashCommandInteraction interaction) {
//        String name = interaction.getOptionStringValueByName("name").orElse("");
//        String content = interaction.getOptionStringValueByName("content").orElse("");
//        String user = interaction.getOptionStringValueByName("user").orElse("");
//        String message = "";
//
//        if(!name.equals("")){
//            ResultSet results = DataBase.getInstance().GetQuotes(DataBase.GetByType.Name, name);
//            try {
//                message = determineSearchResultMessage(results);
//            } catch (SQLException e){
//                System.out.println(e.getMessage());
//            }
//        }
//        else if(!content.equals("")){
//            ResultSet results = DataBase.getInstance().GetQuotes(DataBase.GetByType.Content, content);
//            try {
//                message = determineSearchResultMessage(results);
//            } catch (SQLException e){
//                System.out.println(e.getMessage());
//            }
//        } else if(!user.equals("")){
//            ResultSet results = DataBase.getInstance().GetQuotes(DataBase.GetByType.UID, user);
//            try {
//                message = determineSearchResultMessage(results);
//            } catch (SQLException e){
//                System.out.println(e.getMessage());
//            }
//        }
//        return interaction.createImmediateResponder().setContent(message).respond();
        return null;
    }

    private String determineSearchResultMessage(ResultSet results) throws SQLException {
        boolean isEmpty = true;
        String message = "";
        while(results.next()) {
            isEmpty = false;
            message += results.getString("name") + ": " + results.getString("content") + "\n";
        }
        if(isEmpty){
            message = "No result found.";
        }
        return message;
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> removeCommand(SlashCommandInteraction interaction) {
        String name = interaction.getOptionStringValueByName("name").orElse("");
        JSONObject jsonObject = new JSONObject().put("name", name);

        if (JSONSingleton.removeFromJSON(jsonObject, "quotes", "name")) {
            return interaction.createImmediateResponder().setContent("The quote has been removed.").respond();
        } else {
            return interaction.createImmediateResponder().setContent("The quote has **not** been removed.").respond();
        }
    }

    private  CompletableFuture<InteractionOriginalResponseUpdater> addFeedback(SlashCommandInteraction interaction) {
        String input = interaction.getOptionStringValueByName("input").orElse("");
        String userID = interaction.getUser().getIdAsString();
        String date = interaction.getCreationTimestamp().toString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("input", input);
        jsonObject.put("user-id", userID);
        jsonObject.put("date", date);

        if (JSONSingleton.appendToJSON(jsonObject, "feedback")) {
            return interaction.createImmediateResponder().setContent("Your feedback has been received.").respond();
        } else {
            return interaction.createImmediateResponder().setContent("Your feedback has **not** been received.").respond();
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

        if (JSONSingleton.appendToJSON(jsonObject, "quotes")) {
            return interaction.createImmediateResponder().setContent("Your quote has been added.").respond();
        } else {
            return interaction.createImmediateResponder().setContent("Your quote has **not** been added.").respond();
        }
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> quoteRetrieve(SlashCommandInteraction interaction) {
        JSONObject jsonObject = JSONSingleton.getInstance();

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

    public Command getCommand(String commandID) {
        return commandHashMap.get(commandID);
    }
}
