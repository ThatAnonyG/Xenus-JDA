package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xen.lib.Utils;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;

public class WelcomeText extends Command {
  private GuildModel guildModel;

  public WelcomeText() {
    super("welcomeText");
    setCategory(Categories.CONFIG);
    setDescription("Let's you set a welcome message for new users.");
    setUsage("<Message>");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    guildModel = (GuildModel) ctx.getClient().getDbManager().find(ctx.getEvent().getGuild());

    if (ctx.getArgs().isEmpty()) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() + " The message can't be empty!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    String message = String.join(" ", ctx.getArgs());
    guildModel.getWelcome().setMessage(message);
    guildModel = (GuildModel) ctx.getClient().getDbManager().save(guildModel);

    Utils.sendEm(
            ctx.getEvent().getChannel(),
            ctx.getClient().getTick() + " Welcome message set to:\n\n" +
                    guildModel.getWelcome().getMessage(),
            Utils.Embeds.SUCCESS
    ).queue();
    Utils.sendConfigLog(
            ctx.getEvent(),
            guildModel,
            "Changed Welcome Message",
            "Welcome message changed to\n\n" + guildModel.getWelcome().getMessage()
    );
  }
}
