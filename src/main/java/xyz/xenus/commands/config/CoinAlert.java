package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;

public class CoinAlert extends Command {
  public CoinAlert() {
    super("coinAlert");
    setCategory(Categories.CONFIG);
    setDescription("Let's you disable/enable coin gain alert.");
    setUsage("Use the command to toggle the feature");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    GuildModel guildModel = (GuildModel) ctx.getClient().getDbManager().find(
            ctx.getEvent().getGuild()
    );

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
