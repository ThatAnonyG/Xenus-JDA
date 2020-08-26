package xen.commands.info;

import xen.lib.command.Command;
import xen.lib.command.CommandContext;

public class Help extends Command {
  public Help() {
    super("help");
    setCd(5000);
    setCategory("Info");
    setDescription("Shows you help for a command.");
    setUsage("[Command Name]");
  }

  @Override
  public void run(CommandContext ctx) {

  }
}
