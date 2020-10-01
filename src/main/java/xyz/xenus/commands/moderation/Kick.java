package xyz.xenus.commands.moderation;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.member.Actions;

import java.util.ArrayList;
import java.util.Collection;

public class Kick extends Command {
    public Kick() {
        super("kick");
        setAliases(new String[]{"k"});
        setCd(10000);
        setDescription("Kicks a User");
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        if(!ctx.getEvent().getMember().hasPermission(Permission.KICK_MEMBERS)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    "You don't have permission to kick users",
                    Utils.Embeds.ERROR
            );
            return;
        }
        if(!ctx.getEvent().getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
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

        ctx.getEvent().getGuild().kick(ctx.getArgs().get(1)).queue();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                "Kicked" + ctx.getArgs().get(1) + "from the guild",
                Utils.Embeds.SUCCESS
        );
        return;
    }
}