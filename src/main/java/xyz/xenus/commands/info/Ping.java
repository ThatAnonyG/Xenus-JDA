package xyz.xenus.commands.info;

import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class Ping extends Command {
    public Ping() {
        super("ping");
        setAliases(new String[]{"pong"});
        setCd(5000);
        setDescription("Check the bot ping.");
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                "Pong! My current ping is `" + ctx.getEvent().getJDA().getGatewayPing() + "`ms.",
                Utils.Embeds.BASE
        ).queue();
    }
}
