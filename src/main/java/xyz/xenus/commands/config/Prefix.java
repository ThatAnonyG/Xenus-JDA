package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class Prefix extends Command {
    public Prefix() {
        super("prefix");
        setCategory(Categories.CONFIG);
        setDescription("Lets you to change the bot prefix.");
        setUsage("<New Value>");
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        if (ctx.getArgs().isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Please provide a new prefix value!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        String prefix = ctx.getArgs().get(0);
        if (prefix.length() > 5) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Prefix must have less than 6 letters!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (prefix.equals(ctx.getGuildModel().getPrefix())) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " New prefix cannot be same as old one!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        ctx.getGuildModel().setPrefix(prefix);
        ctx.getGuildModel().save();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() +
                        " Prefix has been changed to `" +
                        ctx.getGuildModel().getPrefix() +
                        "`!",
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(
                ctx.getEvent(),
                ctx.getGuildModel(),
                "Prefix Changed",
                "Prefix changed to `" + prefix + "`"
        );
    }
}
