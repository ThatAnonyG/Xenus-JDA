package xyz.xenus.bot.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class LevelUpAlert extends Command {
    public LevelUpAlert() {
        super("levelUpAlert");
        setCategory(Categories.CONFIG);
        setDescription("Let's you disable/enable level up alert.");
        setUsage("Use command to toggle the feature");
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        ctx.getGuildModel().getEconomy().setLvlUpAlert(!ctx.getGuildModel().getEconomy().isLvlUpAlert());
        ctx.getGuildModel().save();

        String msg = (ctx.getGuildModel().getEconomy().isCoinAlert() ? "Enabled" : "Disabled") +
                " level up alerts in this server!";
        Utils.sendEm(
                ctx.getEvent().getChannel(), ctx.getClient().getTick() + " " + msg, Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(ctx.getEvent(), ctx.getGuildModel(), "Toggled Level Up Alerts", msg);
    }
}
