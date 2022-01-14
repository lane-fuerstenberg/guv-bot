import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    static final String STEVECORD_ID = "301196256116473868";
    static final String EMOTE_CALLING_CARD = "<:Drip:798892442056130590>";
    static final Long ADMIN_ROLE = 516094816040386571L;

    public static void main(String[] args) {
        DiscordApi api = getDiscordApi();
        Optional<Server> server = api.getServerById(STEVECORD_ID);
        CommandHandler commandHandler = new CommandHandler();

        //these commands technically don't need to be even kept in code because the server stores them after first run
        //but like what if they get deleted... I don't want to figure out how to make these again
        SlashCommand quoteAdd =
                SlashCommand.with("quote-add", "Add a new quote",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "name", "The name of the quote you want to save", true),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "content", "The content of the quote you want to save", true)
                        )).createForServer(server.get()).join();

        SlashCommand quote =
                SlashCommand.with("quote", "Call upon already created quotes",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "name", "The name of the quote you want to retrieve", true)
                        )).createForServer(server.get()).join();

        SlashCommand feedback =
                SlashCommand.with("feedback", "Give feedback/suggestions/improvements you want this bot to have",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "input", "Type your feedback here", true)
                        )).createForServer(server.get()).join();

        SlashCommand search =
                SlashCommand.with("search-quote", "Search for a quote",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "name", "Search for quote name (only matches exacts)"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "content", "Search for quotes containing this content"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "user-id", "Search for quotes made by this user")
                        )).createForServer(server.get()).join();

        SlashCommand remove =
                SlashCommand.with("remove-quote", "Remove a quote",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "name", "Search for quote name (only matches exacts)"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "content", "Search for quotes containing this content"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "user-id", "Search for quotes made by this user"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.BOOLEAN, "recursive", "Delete all found quotes (default of false)")
                        )).setDefaultPermission(false).createForServer(server.get()).join();

        Long id = remove.getId();
        new SlashCommandPermissionsUpdater(server.get()).setPermissions(
                Arrays.asList(
                        SlashCommandPermissions.create(ADMIN_ROLE, SlashCommandPermissionType.ROLE, true))
        ).update(id).join();


        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            System.out.println(interaction.getCommandId());
            //command handler finds command from ID then runs it passing interaction

            CompletableFuture<InteractionOriginalResponseUpdater> response = commandHandler.getCommand(interaction.getCommandName()).run(interaction);

            //sync follow-up response for after initial commands response
            //this is just so the bot uses a funny emote every time it responds to a command
            response.thenRun(() -> {
                TextChannel textChannel = interaction.getChannel().get();
                new MessageBuilder().append(EMOTE_CALLING_CARD).send(textChannel);
            });
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