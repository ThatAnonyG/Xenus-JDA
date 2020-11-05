package xyz.xenus.bot.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class CoinAlert extends Command {
    public CoinAlert() {
        super("coinAlert");
        setCategory(Categories.CONFIG);
        setDescription("Let's you disable/enable coin gain alert.");
        setUsage("Use command to toggle the feature");
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        ctx.getGuildModel().getEconomy().setCoinAlert(!ctx.getGuildModel().getEconomy().isCoinAlert());
        ctx.getGuildModel().save();

        String msg = (ctx.getGuildModel().getEconomy().isCoinAlert() ? "Enabled" : "Disabled") +
                " coin gain alerts in this server!";
        Utils.sendEm(
                ctx.getEvent().getChannel(), ctx.getClient().getTick() + " " + msg, Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(ctx.getEvent(), ctx.getGuildModel(), "Toggled Coin Alerts", msg);
    }
}
