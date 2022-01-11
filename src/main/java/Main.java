import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    static final String STEVECORD_ID = "301196256116473868";

    public static void main(String[] args) {
        DiscordApi api = getDiscordApi();
        Optional<Server> server = api.getServerById(STEVECORD_ID);
        CommandHandler commandHandler = new CommandHandler();

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            //command handler finds command from ID then runs it passing interaction
            commandHandler.getCommand(interaction.getCommandId()).run(interaction);
        });
    }

    private static DiscordApi getDiscordApi() {
        String token = null;

        try {
            File file = new File("src/main/resources/token.txt");
            Scanner sc = new Scanner(file);
            token = sc.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        return api;
    }
}