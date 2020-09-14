package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xen.lib.Utils;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;

public class MessageDelete extends Command {
  private GuildModel guildModel;

  public MessageDelete() {
    super("messageDelete");
    setCategory(Categories.CONFIG);
    setDescription("Deletes a mod message after command is ran.");
    setUsage("Use the command to toggle the feature");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    guildModel = (GuildModel) ctx.getClient().getDbManager().find(ctx.getEvent().getGuild());

    guildModel.setMsgDelete(!guildModel.isMsgDelete());
    guildModel = (GuildModel) ctx.getClient().getDbManager().save(guildModel);

    String msg = (guildModel.isMsgDelete() ? "**Enabled:**" : "**Disabled:**") +
            " Delete mod messages after command is ran!";
    Utils.sendEm(
            ctx.getEvent().getChannel(), ctx.getClient().getTick() + " " + msg, Utils.Embeds.SUCCESS
    ).queue();
    Utils.sendConfigLog(ctx.getEvent(), guildModel, "Toggled Delete Message", msg);
  }
}
