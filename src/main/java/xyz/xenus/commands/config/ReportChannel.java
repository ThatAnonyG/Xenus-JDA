package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.util.Optional;

public class ReportChannel extends Command {
    public ReportChannel() {
        super("reportChannel");
        setCategory(Categories.CONFIG);
        setDescription("Let's you set a channel for report logs.");
        setUsage("[Channel Mention | ID] - Leave it blank to disable report logs");
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        if (ctx.getArgs().isEmpty()) {
            ctx.getGuildModel().getIds().setReports("");
            ctx.getClient().getDbManager().save(ctx.getGuildModel());
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getTick() + " Report logs have been disabled!",
                    Utils.Embeds.SUCCESS
            ).queue();
            return;
        }

        Optional<TextChannel> channel = Utils.getChannel(ctx.getEvent().getMessage(), ctx.getArgs());
        if (channel.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No channel found with the given info!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        ctx.getGuildModel().getIds().setReports(channel.get().getId());
        ctx.getClient().getDbManager().save(ctx.getGuildModel());

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " Report logs channel changed to " +
                        channel.get().getAsMention() + "!",
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(
                ctx.getEvent(),
                ctx.getGuildModel(),
                "Changed Report Log Channel",
                "Report logs channel changed to " + channel.get().getAsMention() +
                        " | " + channel.get().getId()
        );
    }
}
