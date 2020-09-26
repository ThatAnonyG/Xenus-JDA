package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;

public class Verify extends Command {
  public Verify() {
    super("verify");
    setCategory(Categories.CONFIG);
    setDescription("Let's you setups a server-wide verification system and allows admins to change settings.");
    setUsage(
            String.join(
                    "\n",
                    "`setup` - Start the verification system setup!",
                    "`remove` - Remove the verification system from the server!"
            )
    );
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    GuildModel guildModel = (GuildModel) ctx.getClient().getDbManager().find(
            ctx.getEvent().getGuild()
    );
  }
}
