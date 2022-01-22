import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    static final String STEVECORD_ID = "301196256116473868";
    static final String EMOTE_CALLING_CARD = "<:Drip:798892442056130590>";
    static final Long MOD_ROLE = 516094816040386571L; //stevecord mod role ID: 525678167818764328
    static final Long BLACKLIST_ROLE = 931608346430152714L;
    static ArrayList<SlashCommand> commands;

    public static void main(String[] args) {
        DiscordApi api = getDiscordApi();
        Optional<Server> server = api.getServerById(STEVECORD_ID);
        CommandHandler commandHandler = new CommandHandler();

        createCommands(server);

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            //command handler finds the respective command then runs it passing interaction
            CompletableFuture<InteractionOriginalResponseUpdater> response = commandHandler.getCommand(interaction.getCommandName()).run(interaction);

            //sync follow-up response for after initial commands response
            //this is just so the bot uses a funny emote every time it responds to a command
            response.thenRun(() -> {
                TextChannel textChannel = interaction.getChannel().get();
                new MessageBuilder().append(EMOTE_CALLING_CARD).send(textChannel);
            });
        });
    }

    private static void createCommands(Optional<Server> server) {
        //these commands technically don't need to be even kept in code because the server stores them after first run
        //but like what if they get deleted... I don't want to figure out how to make these again
        commands = new ArrayList<>();
        commands.add(SlashCommand.with("quote-add", "Add a new quote",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "name", "The name of the quote you want to save", true),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "content", "The content of the quote you want to save", true)
                        )).createForServer(server.get()).join());

        commands.add(SlashCommand.with("quote", "Call upon already created quotes",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "name", "The name of the quote you want to retrieve", true)
                        )).createForServer(server.get()).join());

        commands.add(SlashCommand.with("feedback", "Give feedback/suggestions/improvements you want this bot to have",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "input", "Type your feedback here", true)
                        )).createForServer(server.get()).join());


        SlashCommand remove =
                SlashCommand.with("remove-quote", "Remove a quote by name",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "name", "Search for quote name (only matches exacts)"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "content", "Search for quotes containing this content"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "user-id", "Search for quotes made by this user")
                        )).setDefaultPermission(false).createForServer(server.get()).join();

        SlashCommand search =
                SlashCommand.with("search-quote", "Search for a quote",
                        Arrays.asList(
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "name", "Search for quote name (only matches exacts)"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.STRING, "content", "Search for quotes containing this content"),
                                SlashCommandOption.create
                                        (SlashCommandOptionType.USER, "user", "Search for quotes made by this user")
                        )).createForServer(server.get()).join();

        commands.add(search);

        Long id = remove.getId();
        new SlashCommandPermissionsUpdater(server.get()).setPermissions(
                Arrays.asList(
                        SlashCommandPermissions.create(MOD_ROLE, SlashCommandPermissionType.ROLE, true))
        ).update(id).join();

        commands.add(remove);

        for (int i = 0; i < commands.size(); i++) {
            new SlashCommandPermissionsUpdater(server.get()).setPermissions(
                    Arrays.asList(
                            SlashCommandPermissions.create(BLACKLIST_ROLE, SlashCommandPermissionType.ROLE, false))
            ).update(commands.get(i).getId()).join();
        }
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





//ARCHIVE
/*
        SlashCommand command =
                SlashCommand.with("quote", "Create or call upon quotes",
                    Arrays.asList(
                            SlashCommandOption.create
                            (SlashCommandOptionType.STRING, "quote", "The name of the quote you want to retrieve")
                    )).createForServer(server.get()).join();
 */