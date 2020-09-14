package xen.lib;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import org.jetbrains.annotations.NotNull;
import xen.lib.mongodb.guild.GuildModel;

import java.awt.*;
import java.util.Date;
import java.util.regex.Pattern;

public class Utils {
  public static @NotNull Color getHex() {
    return Color.decode("#d4af37");
  }

  public static @NotNull Color getGreen() {
    return Color.decode("#23d160");
  }

  public static @NotNull Color getRed() {
    return Color.decode("#d94337");
  }

  public static @NotNull Color getProHex() {
    return Color.decode("#00a6ed");
  }

  public static boolean isSnowflake(String input) {
    try {
      MiscUtil.parseSnowflake(input);
      return true;
    } catch (Throwable error) {
      return false;
    }
  }

  public static boolean isInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (Throwable error) {
      return false;
    }
  }

  public static boolean isFloat(String input) {
    try {
      Float.parseFloat(input);
      return true;
    } catch (Throwable error) {
      return false;
    }
  }

  public static boolean partialMatch(String args, String toFind) {
    String formatted = String.join(".*", args.split(" "));
    return Pattern.compile(".*" + formatted + ".*", Pattern.CASE_INSENSITIVE)
            .matcher(toFind).matches();
  }


  public static @NotNull MessageAction sendEm(
          @NotNull TextChannel channel,
          String content,
          Embeds type
  ) {
    MessageEmbed embed = new EmbedBuilder()
            .setColor(type == Embeds.SUCCESS ?
                    getGreen() : type == Embeds.ERROR ?
                    getRed() : type == Embeds.BASE ?
                    getHex() : type == Embeds.PRO ?
                    getProHex() : Color.white)
            .setDescription(content)
            .build();
    return channel.sendMessage(embed);
  }

  public static @NotNull EmbedBuilder embed() {
    return new EmbedBuilder()
            .setFooter("Powered by VorteK Academy", "https://imgur.com/orfFkI6.png")
            .setTimestamp(new Date().toInstant());
  }

  public static void sendModLog(
          @NotNull GuildMessageReceivedEvent event,
          @NotNull GuildModel guildModel,
          String title,
          String reason,
          @NotNull User user
  ) {
    EmbedBuilder logEmbed = embed()
            .setTitle(title)
            .setColor(getHex())
            .addField("User", user.getAsTag() + " | " + user.getId(), false)
            .addField(
                    "Moderator",
                    event.getAuthor().getAsTag() + " | " + event.getAuthor().getId(),
                    false
            )
            .addField("Reason", reason, false);

    if (guildModel.getIds().getLogs().length() < 1) return;
    TextChannel channel = event.getGuild().getTextChannelById(guildModel.getIds().getLogs());
    if (channel == null) return;

    channel.sendMessage(logEmbed.build()).queue();
  }

  public static void sendConfigLog(
          @NotNull GuildMessageReceivedEvent event,
          @NotNull GuildModel guildModel,
          String title,
          String details
  ) {
    EmbedBuilder logEmbed = embed()
            .setTitle(title)
            .setColor(getHex())
            .addField(
                    "Moderator",
                    event.getAuthor().getAsTag() + " | " + event.getAuthor().getId(),
                    false
            )
            .addField("Action", details, false);

    if (guildModel.getIds().getLogs().length() < 1) return;
    TextChannel channel = event.getGuild().getTextChannelById(guildModel.getIds().getLogs());
    if (channel == null) return;

    channel.sendMessage(logEmbed.build()).queue();
  }

  public enum Embeds {
    SUCCESS,
    ERROR,
    BASE,
    PRO
  }
}
