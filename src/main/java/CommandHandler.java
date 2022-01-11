import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashMap;

public class CommandHandler {
    HashMap<Long, Command> commandHashMap;
    final String ECHO_ID = "930410293031473192";

    interface Command {
        void run(SlashCommandInteraction interaction);
    }

    public CommandHandler() {
        commandHashMap = new HashMap<>();

        commandHashMap.put(Long.parseLong(ECHO_ID), (interaction) -> {
            String content = interaction.getFirstOptionStringValue().orElse("");
            System.out.println(content);
            interaction.createImmediateResponder().setContent(content).respond();
        });
    }

    public Command getCommand(long commandID) {
        return commandHashMap.get(commandID);
    }
}
