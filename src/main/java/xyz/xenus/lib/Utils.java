package xyz.xenus.lib;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.mongodb.guild.GuildModel;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
        if (!isSnowflake(guildModel.getIds().getLogs())) return;

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
        if (!isSnowflake(guildModel.getIds().getLogs())) return;

        EmbedBuilder logEmbed = embed()
                .setTitle(title)
                .setColor(getHex())
                .addField(
                        "Moderator",
                        event.getAuthor().getAsTag() + " | " + event.getAuthor().getId(),
                        false
                )
                .addField("Action", details, false);

        TextChannel channel = event.getGuild().getTextChannelById(guildModel.getIds().getLogs());
        if (channel == null) return;

        channel.sendMessage(logEmbed.build()).queue();
    }

    public static Optional<Member> getMember(Message message, List<String> args) {
        if (args.isEmpty()) return Optional.empty();
        if (!message.getMentionedMembers().isEmpty())
            return Optional.of(message.getMentionedMembers().get(0));
        if (isSnowflake(args.get(0)))
            return Optional.ofNullable(message.getGuild().retrieveMemberById(args.get(0)).complete());
        return Optional.empty();
    }

    public static Optional<TextChannel> getChannel(Message message, List<String> args) {
        if (args.isEmpty()) return Optional.empty();
        if (
                !message.getMentionedChannels().isEmpty() &&
                        message.getGuild().getTextChannels().contains(message.getMentionedChannels().get(0))
        )
            return Optional.of(message.getMentionedChannels().get(0));
        if (Utils.isSnowflake(args.get(0)))
            return Optional.ofNullable(message.getGuild().getTextChannelById(args.get(0)));
        return Optional.empty();
    }

    public static Optional<Role> getRole(Message message, List<String> args) {
        System.out.println(args.get(0));
        if (args.isEmpty()) return Optional.empty();
        if (!message.getMentionedRoles().isEmpty())
            return Optional.of(message.getMentionedRoles().get(0));
        if (Utils.isSnowflake(args.get(0)))
            return Optional.ofNullable(message.getGuild().getRoleById(args.get(0)));
        return message.getGuild().getRoles().stream().filter(
                (r) -> Utils.partialMatch(args.get(0).toLowerCase(), r.getName().toLowerCase())
        ).findFirst();
    }

    public static String formatTime(long ms) {
        byte seconds = (byte) Math.ceil((float) (ms / 1000) % 60);
        byte minutes = (byte) Math.ceil((float) (ms / (1000 * 60)) % 60);
        byte hours = (byte) Math.ceil((float) (ms / (1000 * 60 * 60)) % 24);

        return (hours < 10 ? "0" + String.valueOf(hours) : String.valueOf(hours)) + "h " +
                (minutes < 10 ? "0" + String.valueOf(minutes) : String.valueOf(minutes)) + "m " +
                (seconds < 10 ? "0" + String.valueOf(seconds) : String.valueOf(seconds)) + "s";
    }

    public enum Embeds {
        SUCCESS,
        ERROR,
        BASE,
        PRO
    }
}
