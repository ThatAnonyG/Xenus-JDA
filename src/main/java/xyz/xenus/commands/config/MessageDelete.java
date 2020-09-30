package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;

public class MessageDelete extends Command {
    public MessageDelete() {
        super("messageDelete");
        setCategory(Categories.CONFIG);
        setDescription("Deletes a mod message after command is ran.");
        setUsage("Use command to toggle the feature");
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
        setBotPerms(new Permission[]{Permission.MESSAGE_MANAGE});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        ctx.getGuildModel().setMsgDelete(!ctx.getGuildModel().isMsgDelete());
        ctx.setGuildModel((GuildModel) ctx.getClient().getDbManager().save(ctx.getGuildModel()));

        String msg = (ctx.getGuildModel().isMsgDelete() ? "**Enabled:**" : "**Disabled:**") +
                " Delete mod messages after command is ran!";
        Utils.sendEm(
                ctx.getEvent().getChannel(), ctx.getClient().getTick() + " " + msg, Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(ctx.getEvent(), ctx.getGuildModel(), "Toggled Delete Message", msg);
    }
}
