package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;
import xen.lib.utils.Utils;

import java.util.Arrays;

public class ModRole extends Command {
  private GuildModel guildModel;

  public ModRole() {
    super("addMod");
    setCategory(Categories.CONFIG);
    setDescription("Lets you to set/remove an admin role who can use all bot commands.");
    setUsage("[add | del] <Role Mention | ID>");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    guildModel = (GuildModel) ctx
            .getClient()
            .getDbManager()
            .find(ctx.getEvent().getGuild());

    if (!Arrays.asList("add", "del").contains(ctx.getArgs().get(0).toLowerCase()))
      ctx.getArgs().add(0, "add");

    // TODO - Ability to partially match role names
    switch (ctx.getArgs().get(0).toLowerCase()) {
      case "add" -> {
        Role toAdd = ctx.getEvent().getGuild().getRoleById(ctx.getArgs().get(1));
        if (toAdd == null) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() + " No role found with the given info!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }
        guildModel.getIds().getAdminRoles().add(toAdd.getId());
        ctx.getClient().getDbManager().save(guildModel);
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " The " + toAdd.getAsMention() +
                        " role has been added as a bot admin!",
                Utils.Embeds.SUCCESS
        ).queue();
      }

      case "del" -> {
        if (!guildModel.getIds().getAdminRoles().contains(ctx.getArgs().get(0))) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getTick() + " This role is not an Admin role!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }
        guildModel.getIds().getAdminRoles().remove(
                guildModel.getIds().getAdminRoles().stream().filter(
                        (id) -> id.equals(ctx.getArgs().get(0))
                ).toArray(String[]::new)[0]
        );
        ctx.getClient().getDbManager().save(guildModel);
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " The `" + ctx.getArgs().get(0) +
                        "` role has been removed from admin roles!",
                Utils.Embeds.SUCCESS
        ).queue();
      }
    }
  }
}
