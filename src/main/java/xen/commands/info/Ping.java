package xen.commands.info;

import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.utils.Utils;

public class Ping extends Command {
  public Ping() {
    super("ping");
    setDescription("Check the bot ping!");
    setCategory("Info");
    setCd(5000);
    setAliases(new String[]{"pong"});
  }

  @Override
  public void run(CommandContext ctx) {
    Utils.sendEm(
            ctx.getEvent().getChannel(),
            "Pong! My current ping is `" + ctx.getEvent().getJDA().getGatewayPing() + "`ms.",
            Utils.Embeds.BASE
    ).queue();
  }
}
