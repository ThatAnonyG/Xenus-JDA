package xyz.xenus.commands.info;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;
import xyz.xenus.lib.mongodb.guild.Logs;

import java.util.Arrays;
import java.util.Objects;

public class Configs extends Command {
    public Configs() {
        super("configs");
        setCd(5000);
        setDescription("Shows you the current bot configuration for this server.");
    }

    @Override
    public void run(CommandContext ctx) {
        GuildModel guildModel = ctx.getGuildModel();

        // Moderation Settings
        TextChannel logChannel = Utils.isSnowflake(guildModel.getIds().getLogs()) ?
                ctx.getEvent().getGuild().getTextChannelById(
                        guildModel.getIds().getLogs()
                ) :
                null;
        TextChannel rChannel = Utils.isSnowflake(guildModel.getIds().getReports()) ?
                ctx.getEvent().getGuild().getTextChannelById(
                        guildModel.getIds().getReports()
                ) :
                null;
        String[] modRoles = guildModel.getIds().getAdminRoles().stream().map((r) -> {
            Role role = Utils.isSnowflake(r) ? ctx.getEvent().getGuild().getRoleById(r) : null;
            if (role != null) return role.getAsMention();
            return null;
        }).toArray(String[]::new);

        // Mod logs
        String[] logsList = Arrays.stream(Logs.LogTypes.values()).map((l) -> {
            String name = l.name().substring(0, 1).toUpperCase() + l.name().substring(1).toLowerCase();
            return "`" + name + "`: " + (guildModel.getLogs().get(l.name()).isEnabled() ? "Enabled" : "Disabled");
        }).toArray(String[]::new);

        // Categories
        String[] catsList = Arrays.stream(Categories.values()).map((l) -> {
            if (Arrays.asList("config", "dev", "info").contains(l.name().toLowerCase())) return null;

            String name = l.name().substring(0, 1).toUpperCase() + l.name().substring(1).toLowerCase();
            return guildModel.getEnabled().contains(l) ?
                    "`" + name + "`: Enabled" :
                    "`" + name + "`: Disabled";
        }).filter(Objects::nonNull).toArray(String[]::new);

        // Welcome System
        TextChannel joinChannel = Utils.isSnowflake(guildModel.getWelcome().getJoins()) ?
                ctx.getEvent().getGuild().getTextChannelById(guildModel.getWelcome().getJoins()) :
                null;
        TextChannel leaveChannel = Utils.isSnowflake(guildModel.getWelcome().getLeaves()) ?
                ctx.getEvent().getGuild().getTextChannelById(guildModel.getWelcome().getLeaves()) :
                null;
        Role defRole = Utils.isSnowflake(guildModel.getWelcome().getRole()) ?
                ctx.getEvent().getGuild().getRoleById(guildModel.getWelcome().getRole()) :
                null;
        Role verifyRole = Utils.isSnowflake(guildModel.getWelcome().getVerifyRole()) ?
                ctx.getEvent().getGuild().getRoleById(guildModel.getWelcome().getVerifyRole()) :
                null;
        byte verifyChances = guildModel.getWelcome().getVerifyChances();
        long verifyTimeout = guildModel.getWelcome().getVerifyTimeout();

        // Economy
        Role xpLead = Utils.isSnowflake(guildModel.getEconomy().getXpLead()) ?
                ctx.getEvent().getGuild().getRoleById(guildModel.getEconomy().getXpLead()) :
                null;
        String[] blockedChannels = guildModel.getEconomy().getBlocked().stream().map((r) -> {
            TextChannel channel = Utils.isSnowflake(r) ?
                    ctx.getEvent().getGuild().getTextChannelById(r) :
                    null;
            if (channel != null) return channel.getAsMention();
            return null;
        }).toArray(String[]::new);

        MessageEmbed embed = Utils.embed()
                .setTitle("Server Configs")
                .setColor(Utils.getHex())
                .setThumbnail(ctx.getEvent().getGuild().getIconUrl())
                .addField(
                        "Moderation Settings",
                        String.join(
                                "\n",
                                "**Mod Logs:** " +
                                        (logChannel == null ? "Not Set" : logChannel.getAsMention()),
                                "**Report Logs:** " +
                                        (rChannel == null ? "Not Set" : rChannel.getAsMention()),
                                "**Mod Roles:** " +
                                        (modRoles.length == 0 ?
                                                "Not Set" :
                                                String.join(" | ", modRoles)),
                                "**Delete mod message after command:** " +
                                        (guildModel.isMsgDelete() ? "Enabled" : "Disabled")
                        ),
                        false
                )
                .addField("Mod Logs", String.join("\n", logsList), false)
                .addField("Categories", String.join("\n", catsList), false)
                .addField(
                        "Welcome System",
                        String.join(
                                "\n",
                                "**Join Logs:** " +
                                        (joinChannel == null ? "Not Set" : joinChannel.getAsMention()),
                                "**Leave Logs:** " +
                                        (leaveChannel == null ? "Not Set" : leaveChannel.getAsMention()),
                                "**Default Role:** " +
                                        (defRole == null ? "Not Set" : defRole.getAsMention()),
                                "**Welcome Message:**\n" + guildModel.getWelcome().getMessage(),
                                "**Verified Role:** " +
                                        (verifyRole == null ? "Not Set" : verifyRole.getAsMention()),
                                "**Captcha Chances:** " +
                                        (verifyChances == 0 ? "Not Set" : verifyChances),
                                "**Verify Timeout:** " +
                                        (verifyTimeout == 0 ? "Not Set" : verifyTimeout)
                        ),
                        false
                )
                .addField(
                        "Economy",
                        String.join(
                                "\n",
                                "**XP Gain Rate:** " + guildModel.getEconomy().getXpRate(),
                                "**XP Lead Role:** " +
                                        (xpLead == null ? "Not Set" : xpLead.getAsMention()),
                                "**Blocked Channels:** " +
                                        (blockedChannels.length == 0 ?
                                                "Not Set" :
                                                String.join(" | ", blockedChannels)),
                                "**Level Up Alerts:** " +
                                        (guildModel.getEconomy().isLvlUpAlert() ? "Enabled" : "Disabled"),
                                "**Coin Gain Alerts:** " +
                                        (guildModel.getEconomy().isCoinAlert() ? "Enabled" : "Disabled")
                        ),
                        false
                )
                .setFooter("Prefix: " + guildModel.getPrefix(), "https://imgur.com/orfFkI6.png")
                .build();

        ctx.getEvent().getChannel().sendMessage(embed).queue();
    }
}
