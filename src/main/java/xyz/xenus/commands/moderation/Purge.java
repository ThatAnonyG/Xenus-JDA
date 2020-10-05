package xyz.xenus.commands.moderation;

import net.dv8tion.jda.api.Permission;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

// Todo - Complete the command
public class Purge extends Command {
    public Purge() {
        super("purge");
        setCategory(Categories.MODERATION);
        setDescription("Bulk delete messages in a channel.");
        setUsage("<Amount>");
        setPerms(new Permission[]{Permission.MESSAGE_MANAGE});
        setBotPerms(new Permission[]{Permission.MESSAGE_MANAGE});
    }

    @Override
    public void run(CommandContext ctx) {
        if (ctx.getArgs().isEmpty() || !Utils.isInteger(ctx.getArgs().get(0))) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Please enter a valid amount!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        int amount = Integer.parseInt(ctx.getArgs().get(0));
        while (amount <= 100) {
        }
    }
}
