import Database.DataBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        commandHashMap.put(QUOTE_COMMAND, interaction -> {
            try {
                return quoteRetrieve(interaction);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        });

        commandHashMap.put(QUOTE_ADD_COMMAND, interaction -> {
            try {
                return quoteAdd(interaction);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        });
        commandHashMap.put(FEEDBACK_COMMAND, interaction -> {
            try {
                return addFeedback(interaction);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        });
        commandHashMap.put(REMOVE_QUOTE_COMMAND, interaction -> {
            try {
                return removeCommand(interaction);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        });
        commandHashMap.put(SEARCH_QUOTE_COMMAND, interaction ->{
                try {
                    return searchCommand(interaction);
                } catch (SQLException e){
                    System.out.println(e.getMessage());
                    return null;
                }
        });
        //todo: blacklist user command
        //todo: view quotes
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> searchCommand(SlashCommandInteraction interaction) throws SQLException {
        String name = interaction.getOptionStringValueByName("name").orElse("");
        String content = interaction.getOptionStringValueByName("content").orElse("");
        String user = interaction.getOptionStringValueByName("user").orElse("");
        String message = "No results found!";

        if(!name.equals("")){
            HashMap<Long, ArrayList<DataBase.Quote>> results = DataBase.getInstance().GetQuotes(DataBase.GetByType.Name, name);
            if(!results.isEmpty()){
                message = determineSearchResultMessage(results);
            }
        }
        else if(!content.equals("")){
            HashMap<Long, ArrayList<DataBase.Quote>> results = DataBase.getInstance().GetQuotes(DataBase.GetByType.Content, content);
            if(!results.isEmpty()){
                message = determineSearchResultMessage(results);
            }
        } else if(!user.equals("")){
            HashMap<Long, ArrayList<DataBase.Quote>> results = DataBase.getInstance().GetQuotes(DataBase.GetByType.UID, user);
            if(!results.isEmpty()){
                message = determineSearchResultMessage(results);
            }
        }
        return interaction.createImmediateResponder().setContent(message).respond();
    }

    private String determineSearchResultMessage(HashMap<Long, ArrayList<DataBase.Quote>> results) {
        StringBuilder message = new StringBuilder();
        for (long uid: results.keySet()) {
            for(int i = 0; i < results.get(uid).size(); i++){
                message.append(results.get(uid).get(i).Name + " : " + results.get(uid).get(i).Content + "\n");
            }
        }
        return message.toString();
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> removeCommand(SlashCommandInteraction interaction) throws SQLException {
        String name = interaction.getOptionStringValueByName("name").orElse("");
        String message = "Removed quote successfully";

        boolean removed = DataBase.getInstance().RemoveEntry(DataBase.GetByType.Name, name);

        if(!removed){
            message = "Quote **not** removed successfully";
        }

        return interaction.createImmediateResponder().setContent(message).respond();
    }

    private  CompletableFuture<InteractionOriginalResponseUpdater> addFeedback(SlashCommandInteraction interaction) throws SQLException {
        String input = interaction.getOptionStringValueByName("input").orElse("");
        Long userID = interaction.getUser().getId();
        String message = "Feedback added! Thank You";

        boolean added = DataBase.getInstance().AddFeedback(userID, input);

        if(!added){
            message = "Feedback **not** added!";
        }

        return interaction.createImmediateResponder().setContent(message).respond();
    }

    private  CompletableFuture<InteractionOriginalResponseUpdater> quoteAdd(SlashCommandInteraction interaction) throws SQLException {
        String name = interaction.getOptionStringValueByName("name").orElse("");
        String content = interaction.getOptionStringValueByName("content").orElse("");
        Long userID = interaction.getUser().getId();
        String message = "Quote created successfully";

        boolean created = DataBase.getInstance().CreateEntry(userID, name, content);

        if(!created){
            message = "Quote **not** created successfully";
        }

        return interaction.createImmediateResponder().setContent(message).respond();
    }

    private CompletableFuture<InteractionOriginalResponseUpdater> quoteRetrieve(SlashCommandInteraction interaction) throws SQLException {
        String quoteName = interaction.getFirstOptionStringValue().orElse("");

        HashMap<Long, ArrayList<DataBase.Quote>> results = DataBase.getInstance().GetQuotes(DataBase.GetByType.Name, quoteName);

        if(results.isEmpty()) return interaction.createImmediateResponder().setContent("No matching quotes found.").respond();

        String content = determineSearchResultMessage(results);
        return interaction.createImmediateResponder().setContent(content).respond();
    }

    public Command getCommand(String commandID) {
        return commandHashMap.get(commandID);
    }
}
