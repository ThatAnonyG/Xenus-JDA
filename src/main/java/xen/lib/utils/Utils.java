package xen.lib.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import xen.lib.mongodb.guild.GuildModel;

import java.awt.*;
import java.util.Date;

public class Utils {
  public static Color getHex() {
    return Color.decode("#d4af37");
  }

  public static Color getGreen() {
    return Color.decode("#23d160");
  }

  public static Color getRed() {
    return Color.decode("#d94337");
  }

  public static Color getProHex() {
    return Color.decode("#00a6ed");
  }

  public static MessageAction sendEm(TextChannel channel, String content, Embeds type) {
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

  public static boolean isManageable(Member client, Member user) {
    if (
            user.getUser().getId().equals(user.getGuild().getOwnerId()) ||
                    user.getUser().getId().equals(client.getUser().getId())
    ) return false;
    if (user.getGuild().getOwnerId().equals(client.getUser().getId())) return true;
    return client.getRoles().get(0).getPosition() < user.getRoles().get(0).getPosition();
  }

  public static MessageAction sendLog(
          GuildMessageReceivedEvent event,
          GuildModel guildModel,
          Logs type,
          String title,
          String reason
  ) {
    return null;
  }

  public static MessageAction sendLog(
          GuildMessageReceivedEvent event,
          GuildModel guildModel,
          Logs type,
          String title,
          String reason,
          User user
  ) {
    EmbedBuilder logEmbed = new EmbedBuilder()
            .setTitle(title)
            .setTimestamp(new Date().toInstant())
            .setFooter(
                    event.getJDA().getSelfUser().getName(),
                    event.getJDA().getSelfUser().getAvatarUrl()
            );

    if (type == Logs.MOD) {
      logEmbed
              .addField("User", user.getAsTag() + " | " + user.getId(), false)
              .addField(
                      "Moderator",
                      event.getAuthor().getAsTag() + " | " + event.getAuthor().getId(),
                      false
              )
              .addField("Reason", reason, false);
    } else if (type == Logs.TOGGLE) {

    } else if (type == Logs.BASE) {

    }

    TextChannel channel = event.getGuild().getTextChannelById(guildModel.getIds().getLogs());
    if (channel == null) return null;

    return channel.sendMessage(logEmbed.build());
  }

  public enum Logs {
    MOD,
    TOGGLE,
    BASE
  }

  public enum Embeds {
    SUCCESS,
    ERROR,
    BASE,
    PRO
  }
}
