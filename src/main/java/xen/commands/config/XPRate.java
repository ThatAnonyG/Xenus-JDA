package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xen.lib.Utils;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;

public class XPRate extends Command {
  public GuildModel guildModel;

  public XPRate() {
    super("xpRate");
    setCategory(Categories.CONFIG);
    setDescription("Let's you set a custom XP gain rate.");
    setUsage("<New Rate>");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    guildModel = (GuildModel) ctx.getClient().getDbManager().find(ctx.getEvent().getGuild());

    if (ctx.getArgs().isEmpty()) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() + " Please enter a new XP rate!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }
    if (
            !Utils.isFloat(ctx.getArgs().get(0)) ||
                    Float.parseFloat(ctx.getArgs().get(0)) < 1 ||
                    Float.parseFloat(ctx.getArgs().get(0)) > 4
    ) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() +
                      " XP rate must be a number/decimal within the range of 1-4!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    guildModel.getEconomy().setXpRate(Float.parseFloat(ctx.getArgs().get(0)));
    guildModel = (GuildModel) ctx.getClient().getDbManager().save(guildModel);

    String msg = "Server XP rate updated to `" + guildModel.getEconomy().getXpRate() + "`!";
    Utils.sendEm(
            ctx.getEvent().getChannel(),
            ctx.getClient().getTick() + " " + msg,
            Utils.Embeds.SUCCESS
    ).queue();
    Utils.sendConfigLog(ctx.getEvent(), guildModel, "Updated XP Rate", msg);
  }
}
