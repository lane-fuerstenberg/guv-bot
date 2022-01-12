import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
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

//        SlashCommand command =
//                SlashCommand.with("quote", "Create or call upon quotes", Arrays.asList(
//                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, "edit", "Edits a channel", Arrays.asList(
//                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add", "Adds quote", Arrays.asList(
//
//                                )
//                                )
//                        )
//                ).createForServer(server.get()).join();

//this one
//        SlashCommand command =
//                SlashCommand.with("quote", "Create or call upon quotes",
//                    Arrays.asList(
//                            SlashCommandOption.create
//                            (SlashCommandOptionType.STRING, "quote", "The name of the quote you want to retrieve"),
//                            SlashCommandOption.createWithOptions()
//                                    (SlashCommandOptionType.STRING, "quote2", "The name of the quote2 you want to retrieve")
//                    )).createForServer(server.get()).join();

//        SlashCommand command =
//                SlashCommand.with("quote", "Create or call upon quotes",
//                        Arrays.asList(
//                                SlashCommandOption.createWithOptions
//                                (SlashCommandOptionType.SUB_COMMAND, "add", "Add a new quote"),
//                                        SlashCommandOption.create
//                                        (SlashCommandOptionType.STRING, "name", "The name of the quote you want to save"),
//                                        SlashCommandOption.create
//                                        (SlashCommandOptionType.STRING, "content", "The content of the quote you want to save")))
//                                        .createForServer(server.get()).join();

//        SlashCommand command =
//            SlashCommand.with("channel", "A command dedicated to channels",
//                Arrays.asList(
//                    SlashCommandOption.create(SlashCommandOptionType.STRING, "quote", "The name of the quote you want to retrieve"),
//
//                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, "edit", "Edits a channel",
//                        Arrays.asList(
//                            SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "allow", "Allows a permission to a user for a channel",
//                                Arrays.asList(
//                                    SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "The channel to modify", true),
//                                    SlashCommandOption.create(SlashCommandOptionType.USER, "USER", "The user which permissions should be changed", true),
//                                    SlashCommandOption.createWithChoices(SlashCommandOptionType.INTEGER, "PERMISSION", "The permission to allow", true,
//                                        Arrays.asList(
//                                                SlashCommandOptionChoice.create("MANAGE", 0),
//                                                SlashCommandOptionChoice.create("SHOW", 1)
//                                        )
//                                    )
//                                )
//                            )
//                        )
//                    )
//                )
//            ).createForServer(server.get()).join();


        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            System.out.println(interaction.getCommandId());
            //command handler finds command from ID then runs it passing interaction
            CompletableFuture<InteractionOriginalResponseUpdater> response = commandHandler.getCommand(interaction.getCommandId()).run(interaction);

            //sync follow-up response for after initial commands response
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





//ARCHIVE
/*
        SlashCommand command =
                SlashCommand.with("quote", "Create or call upon quotes",
                    Arrays.asList(
                            SlashCommandOption.create
                            (SlashCommandOptionType.STRING, "quote", "The name of the quote you want to retrieve")
                    )).createForServer(server.get()).join();
 */