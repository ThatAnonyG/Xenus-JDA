package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;

public class LevelUpAlert extends Command {
  public LevelUpAlert() {
    super("levelUpAlert");
    setCategory(Categories.CONFIG);
    setDescription("Let's you disable/enable level up alert.");
    setUsage("Use the command to toggle the feature");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    GuildModel guildModel = (GuildModel) ctx.getClient().getDbManager().find(
            ctx.getEvent().getGuild()
    );

    guildModel.getEconomy().setLvlUpAlert(!guildModel.getEconomy().isLvlUpAlert());
    guildModel = (GuildModel) ctx.getClient().getDbManager().save(guildModel);

    String msg = (guildModel.getEconomy().isCoinAlert() ? "Enabled" : "Disabled") +
            " level up alerts in this server!";
    Utils.sendEm(
            ctx.getEvent().getChannel(), ctx.getClient().getTick() + " " + msg, Utils.Embeds.SUCCESS
    ).queue();
    Utils.sendConfigLog(ctx.getEvent(), guildModel, "Toggled Level Up Alerts", msg);
  }
}
