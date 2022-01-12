import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class CommandHandler {
    HashMap<Long, Command> commandHashMap;
    final String EMOTE_CALLING_CARD = "<:C12:710506476208390216>";
    final Long ECHO_ID = 930410293031473192L;
    final Long QUOTE_ID = 930450163095449601L;
    final Long QUOTE_ADD_ID = 930779269817303050L;

    interface Command {
        void run(SlashCommandInteraction interaction);
    }

    public CommandHandler() {
        commandHashMap = new HashMap<>();
        commandHashMap.put(ECHO_ID, interaction -> echo(interaction));
        commandHashMap.put(QUOTE_ID, interaction ->  quoteRetrieve(interaction));
        commandHashMap.put(QUOTE_ADD_ID, interaction -> quoteAdd(interaction));
    }

    private void quoteAdd(SlashCommandInteraction interaction) {
        String name = interaction.getOptionStringValueByName("name").orElse("");
        String content = interaction.getOptionStringValueByName("content").orElse("");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("content", content);

        if (JSONWrapperSingleton.appendToJSON(jsonObject)) {
            interaction.createImmediateResponder().setContent("Your quote has been added.").respond();
        } else {
            interaction.createImmediateResponder().setContent("Your quote has **not** been added.").respond();
        }
    }

    private void quoteRetrieve(SlashCommandInteraction interaction) {
        JSONObject jsonObject = JSONWrapperSingleton.getInstance();

        JSONArray quotes = jsonObject.getJSONArray("quotes");
        String quoteName = interaction.getFirstOptionStringValue().orElse("");

        for (int i = 0; i < quotes.length(); i++) {
            JSONObject quoteJSONObject = quotes.getJSONObject(i);

            if (quoteJSONObject.getString("name").equals(quoteName)) {
                String content = quoteJSONObject.getString("content");
                interaction.createImmediateResponder().setContent(content).respond();
            }
        }

        interaction.createImmediateResponder().setContent("No matching quotes found.").respond();
    }

    private void echo(SlashCommandInteraction interaction) {
        String content = interaction.getFirstOptionStringValue().orElse("");
        System.out.println(content);
        interaction.createImmediateResponder().setContent(content + "\n" + EMOTE_CALLING_CARD).respond();
    }

    public Command getCommand(long commandID) {
        return commandHashMap.get(commandID);
    }
}
