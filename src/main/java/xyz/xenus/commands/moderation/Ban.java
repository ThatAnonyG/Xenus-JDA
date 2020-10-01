package xyz.xenus.commands.moderation;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class Ban extends Command {
    public Ban() {
        super("ban");
        setAliases(new String[]{"b"});
        setCd(10000);
        setDescription("Bans a User");
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        if(!ctx.getEvent().getMember().hasPermission(Permission.BAN_MEMBERS)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    "You don't have permission to ban users",
                    Utils.Embeds.ERROR
            );
            return;
        }
        if(!ctx.getEvent().getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    "I don't have permission to ban users",
                    Utils.Embeds.ERROR
            );
            return;
        }
        if(!(ctx.getArgs().get(1) == null)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    "No User Provided",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        ctx.getEvent().getGuild().ban(ctx.getArgs().get(1), Integer.parseInt((!(ctx.getArgs().get(2) == null)) ? ctx.getArgs().get(2) : "No Reason Provided")).queue();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                "Banned" + ctx.getArgs().get(1) + "from the guild",
                Utils.Embeds.SUCCESS
        );
        return;
    }
}