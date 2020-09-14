package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xen.lib.Utils;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;

public class CoinAlert extends Command {
  private GuildModel guildModel;

  public CoinAlert() {
    super("coinAlert");
    setCategory(Categories.CONFIG);
    setDescription("Let's you disable/enable coin gain alert.");
    setUsage("Use the command to toggle the feature");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    guildModel = (GuildModel) ctx.getClient().getDbManager().find(ctx.getEvent().getGuild());

    guildModel.getEconomy().setCoinAlert(!guildModel.getEconomy().isCoinAlert());
    guildModel = (GuildModel) ctx.getClient().getDbManager().save(guildModel);

    String msg = (guildModel.getEconomy().isCoinAlert() ? "Enabled" : "Disabled") +
            " coin gain alerts in this server!";
    Utils.sendEm(
            ctx.getEvent().getChannel(), ctx.getClient().getTick() + " " + msg, Utils.Embeds.SUCCESS
    ).queue();
    Utils.sendConfigLog(ctx.getEvent(), guildModel, "Toggled Coin Alerts", msg);
  }
}
