package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;

public class AddMod extends Command {
  public AddMod() {
    super("addMod");
    setCategory(Categories.CONFIG);
    setDescription("Lets you to set/remove an admin role who can use all bot commands.");
    setUsage("<Role Mention | ID>");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    
  }
}
