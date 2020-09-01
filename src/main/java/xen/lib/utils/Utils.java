package xen.lib.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import xen.lib.mongodb.guild.GuildModel;

import java.awt.*;
import java.util.Date;

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

  public static void sendModLog(
          @NotNull GuildMessageReceivedEvent event,
          @NotNull GuildModel guildModel,
          String title,
          String reason,
          @NotNull User user
  ) {
    EmbedBuilder logEmbed = new EmbedBuilder()
            .setTitle(title)
            .setColor(getHex())
            .addField("User", user.getAsTag() + " | " + user.getId(), false)
            .addField(
                    "Moderator",
                    event.getAuthor().getAsTag() + " | " + event.getAuthor().getId(),
                    false
            )
            .addField("Reason", reason, false)
            .setTimestamp(new Date().toInstant())
            .setFooter(
                    event.getJDA().getSelfUser().getName(),
                    event.getJDA().getSelfUser().getAvatarUrl()
            );

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
    EmbedBuilder logEmbed = new EmbedBuilder()
            .setTitle(title)
            .setColor(getHex())
            .addField(
                    "Moderator",
                    event.getAuthor().getAsTag() + " | " + event.getAuthor().getId(),
                    false
            )
            .addField("Action", details, false)
            .setTimestamp(new Date().toInstant())
            .setFooter(
                    event.getJDA().getSelfUser().getName(),
                    event.getJDA().getSelfUser().getAvatarUrl()
            );

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
