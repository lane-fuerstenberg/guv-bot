import JSONSingleton.QuoteJSONSingleton;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashMap;

public class ListAllEmbed {
    private HashMap<String, String> nameContentMap = new HashMap<String, String>();

    public ListAllEmbed(){
        JSONObject jsonObject = QuoteJSONSingleton.getInstance();

        JSONArray quotes = jsonObject.getJSONArray("quotes");
        for (int i = 0; i < quotes.length(); i++) {
            JSONObject quoteJSONObject = quotes.getJSONObject(i);
            nameContentMap.put(quoteJSONObject.getString("name"), quoteJSONObject.getString("content"));
        }
    }

    public EmbedBuilder CreateEmbed(){
        CustomEmbedBuilder embed = new CustomEmbedBuilder();
        embed
                .setTitle("Here is a list of all quotes.")
                .setAuthor("guv", "", "https://cdn.discordapp.com/emojis/798892442056130590.webp?size=128&quality=lossless")
                .addInlineField("Name", "Value")
                .addInlineField("Name", "Value")
                .addInlineField("Name", "Value")
                .addInlineField("Name", "Value")
                .addInlineField("Name", "Value")
                .addInlineField("Name", "Value")
                .addInlineField("Name", "Value")
                .addInlineField("Name", "Value")
                .addInlineField("Name", "Value")
                .setColor(Color.YELLOW)
                .setFooter("Page/Count");
        /*EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Here is a list of all quotes.")
                .setAuthor("guv", "", "https://cdn.discordapp.com/emojis/798892442056130590.webp?size=128&quality=lossless")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .addInlineField("Quote Name", "Quote up to 16 chars")
                .setColor(Color.YELLOW)
                .setFooter("Page/Count");*/

        /*EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Title")
                .setDescription("Description")
                .setAuthor("Author Name", "", "https://cdn.discordapp.com/embed/avatars/0.png")
                .addField("A field", "Some text inside the field")
                .addInlineField("An inline field", "More text")
                .addInlineField("Another inline field", "Even more text")
                .setColor(Color.YELLOW)
                .setFooter("Footer", "https://cdn.discordapp.com/embed/avatars/1.png")
                .setImage("https://i.imgur.com/QYbXmQU.png")
                .setThumbnail("https://i.imgur.com/QYbXmQU.png");*/

        return embed;
    }

    public class CustomEmbedBuilder extends EmbedBuilder{
        @Override
        public EmbedBuilder addInlineField(String name, String value) {
            for(int i = 0; i < nameContentMap.size() - 1; i++) {
                return super.addInlineField(name, value);
            }

            return null;
        }
    }
}
