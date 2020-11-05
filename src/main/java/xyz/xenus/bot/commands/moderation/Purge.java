package xyz.xenus.bot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        List<Message> history = ctx.getEvent().getChannel().getHistory().retrievePast(amount).complete();
        OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minus(2, ChronoUnit.WEEKS);

        history.removeIf((x) -> x.getTimeCreated().isBefore(twoWeeksAgo) || x.isPinned());
        ctx.getEvent().getChannel().deleteMessages(history).queue();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " Successfully deleted " + history.size() + " messages!",
                Utils.Embeds.SUCCESS
        ).queue();
    }
}
