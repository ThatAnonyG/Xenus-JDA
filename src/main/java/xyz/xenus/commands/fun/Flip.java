package xyz.xenus.commands.fun;

import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class Flip extends Command {
  public Flip() {
    super("flip");
    setCategory(Categories.FUN);
    setDescription("Flips a coin for you.");
  }

  @Override
  public void run(CommandContext ctx) {
    Utils.sendEm(
            ctx.getEvent().getChannel(),
            "You flipped a coin and got `" +
                    (Math.random() * 100 < 50 ? "tails" : "heads") + "`!",
            Utils.Embeds.BASE
    ).queue();
  }
}
