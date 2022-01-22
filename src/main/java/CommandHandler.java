import Database.DataBase;
import JSONSingleton.FeedbackJSONSingleton;
import JSONSingleton.QuoteJSONSingleton;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
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
        commandHashMap.put(QUOTE_COMMAND, interaction -> {
            try {
                return quoteRetrieve(interaction);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        });

        commandHashMap.put(QUOTE_ADD_COMMAND, interaction -> quoteAdd(interaction));
        commandHashMap.put(FEEDBACK_COMMAND, interaction -> addFeedback(interaction));
        commandHashMap.put(REMOVE_QUOTE_COMMAND, interaction -> removeCommand(interaction));

        commandHashMap.put(SEARCH_QUOTE_COMMAND, interaction -> {
            try {
                return searchQuote(interaction);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        });
        //todo: blacklist user command
        //todo: view quotes
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> searchQuote(SlashCommandInteraction interaction) throws SQLException {
        String name = interaction.getOptionStringValueByName("name").orElse("");
        String content = interaction.getOptionStringValueByName("content").orElse("");
        User user = (User) interaction.requestOptionUserValueByName("user").orElse(null);

        if(!name.equals("")){
            ResultSet results = DataBase.getInstance().GetQuotes(DataBase.GetByType.Name, name);
            while (results.next()){
                System.out.println(results.getString("Content"));
            }
        }
        else if(!content.equals("")){
            ResultSet results = DataBase.getInstance().GetQuotes(DataBase.GetByType.Content, content);
            while (results.next()){
                System.out.println(results.getString("Content"));
            }
        }
        else if(user != null){
            String id = String.valueOf(user.getId());
            ResultSet results = DataBase.getInstance().GetQuotes(DataBase.GetByType.UID, id);
            while (results.next()){
                System.out.println(results.getString("Content"));
            }
        }

        return interaction.createImmediateResponder().setContent("No matching quotes found.").respond();
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> removeCommand(SlashCommandInteraction interaction) {
        String name = interaction.getOptionStringValueByName("name").orElse("");

        //todo: put in request for database to remove the thing by name
        //database not available at current time

        return interaction.createImmediateResponder().setContent("The quote has **not** been removed.").respond();
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

        if (QuoteJSONSingleton.appendToJSON(jsonObject)) {
            return interaction.createImmediateResponder().setContent("Your quote has been added.").respond();
        } else {
            return interaction.createImmediateResponder().setContent("Your quote has **not** been added.").respond();
        }
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> quoteRetrieve(SlashCommandInteraction interaction) throws SQLException {
        String quoteName = interaction.getFirstOptionStringValue().orElse("");

        ResultSet results = DataBase.getInstance().GetQuotes(DataBase.GetByType.Name, quoteName);

        if(results == null) return interaction.createImmediateResponder().setContent("No matching quotes found.").respond();

        String content = results.getString("Content");
        return interaction.createImmediateResponder().setContent(content).respond();
    }

    public Command getCommand(String commandID) {
        return commandHashMap.get(commandID);
    }
}
