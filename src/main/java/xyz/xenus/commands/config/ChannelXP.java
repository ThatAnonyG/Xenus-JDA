package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class ChannelXP extends Command {
    public ChannelXP() {
        super("channelXP");
        setCategory(Categories.CONFIG);
        setDescription("Let's you disable/enable XP gain in the current channel.");
        setUsage("<Y | N>");
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();

        if (!ctx.hasSub(new String[]{"y", "n"}, 0)) {
            Utils.sendEm(
                    channel,
                    ctx.getClient().getCross() +
                            " Use a proper option - `Y` for enable and `N` for disable!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        switch (ctx.getArgs().get(0).toLowerCase()) {
            case "y" -> {
                if (!ctx.getGuildModel().getEconomy().getBlocked().contains(channel.getId())) {
                    Utils.sendEm(
                            channel,
                            ctx.getClient().getCross() +
                                    " XP gain is already enabled in this channel!",
                            Utils.Embeds.ERROR
                    ).queue();
                    return;
                }

                ctx.getGuildModel().getEconomy().getBlocked().remove(channel.getId());
                ctx.getClient().getDbManager().save(ctx.getGuildModel());

                Utils.sendEm(
                        channel,
                        ctx.getClient().getTick() +
                                " Enabled XP gain in this channel!",
                        Utils.Embeds.SUCCESS
                ).queue();
                Utils.sendConfigLog(
                        ctx.getEvent(),
                        ctx.getGuildModel(),
                        "Removed No XP Channel",
                        "XP gain enabled in " + channel.getAsMention() + " | " + channel.getId()
                );
            }

            case "n" -> {
                if (ctx.getGuildModel().getEconomy().getBlocked().contains(channel.getId())) {
                    Utils.sendEm(
                            channel,
                            ctx.getClient().getCross() +
                                    " XP gain is already disabled in this channel!",
                            Utils.Embeds.ERROR
                    ).queue();
                    return;
                }

                ctx.getGuildModel().getEconomy().getBlocked().add(channel.getId());
                ctx.getClient().getDbManager().save(ctx.getGuildModel());

                Utils.sendEm(
                        channel,
                        ctx.getClient().getTick() +
                                " Disabled XP gain in this channel!",
                        Utils.Embeds.SUCCESS
                ).queue();
                Utils.sendConfigLog(
                        ctx.getEvent(),
                        ctx.getGuildModel(),
                        "Added No XP Channel",
                        "XP gain disabled in " + channel.getAsMention() + " | " + channel.getId()
                );
            }
        }
    }
}
