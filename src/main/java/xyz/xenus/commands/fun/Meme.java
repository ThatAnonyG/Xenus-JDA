package xyz.xenus.commands.fun;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Meme extends Command {
  public Meme() {
    super("meme");
    setCategory(Categories.FUN);
    setCd(2000);
    setDescription("Have a good laugh over some funny memes.");
  }

  @Override
  public void run(CommandContext ctx) throws IOException, ParseException {
    URL apiString = new URL("http://meme-api.herokuapp.com/gimme");
    HttpURLConnection con = (HttpURLConnection) apiString.openConnection();
    con.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)
    );
    StringBuilder content = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    con.disconnect();
    in.close();

    JSONParser parser = new JSONParser();
    JSONObject data = (JSONObject) parser.parse(content.toString());

    MessageEmbed meme = Utils.embed()
            .setImage(data.get("url").toString())
            .setColor(Utils.getHex())
            .setTitle("Meme From " +
                    data.get("subreddit").toString(), data.get("postLink").toString())
            .setDescription(String.join(
                    "\n",
                    "**Title:** " + data.get("title").toString(),
                    "**Author:** [" + data.get("author").toString() +
                            "](https://www.reddit.com/user/" + data.get("author").toString() + ")",
                    "**Ups:** " + data.get("ups").toString()
            ))
            .build();
    ctx.getEvent().getChannel().sendMessage(meme).queue();
  }
}
