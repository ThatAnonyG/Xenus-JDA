package xen.commands.info;

import org.jetbrains.annotations.NotNull;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.utils.Utils;

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
