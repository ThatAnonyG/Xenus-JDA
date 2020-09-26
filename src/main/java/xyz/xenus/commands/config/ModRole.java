package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;

import java.util.concurrent.atomic.AtomicReference;

public class ModRole extends Command {
  public ModRole() {
    super("modRole");
    setCategory(Categories.CONFIG);
    setDescription("Lets you to set/remove an admin role who can use all bot commands.");
    setUsage("[add | del] <Role Mention | ID>");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    GuildModel guildModel = (GuildModel) ctx.getClient().getDbManager().find(
            ctx.getEvent().getGuild()
    );

    if (!ctx.hasSub(new String[]{"add", "del"}, 0)) ctx.getArgs().add(0, "add");
    String sub = ctx.getArgs().remove(0);
    if (ctx.getArgs().isEmpty()) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() + " No ID or role name provided!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    AtomicReference<Role> role = new AtomicReference<>();
    if (!ctx.getEvent().getMessage().getMentionedRoles().isEmpty())
      role.set(ctx.getEvent().getMessage().getMentionedRoles().get(0));
    if (role.get() == null && Utils.isSnowflake(ctx.getArgs().get(0)))
      role.set(ctx.getEvent().getGuild().getRoleById(ctx.getArgs().get(0)));
    if (role.get() == null)
      ctx.getEvent().getGuild().getRoles().stream().filter(
              (r) -> Utils.partialMatch(String.join(" ", ctx.getArgs()), r.getName())
      ).findFirst().ifPresent(role::set);

    if (role.get() == null) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() + " No role found with the given info!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    switch (sub) {
      case "add" -> {
        if (guildModel.getIds().getAdminRoles().contains(role.get().getId())) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() + " This role is already an admin role!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }

        guildModel.getIds().getAdminRoles().add(role.get().getId());
        ctx.getClient().getDbManager().save(guildModel);

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " The " + role.get().getAsMention() +
                        " role has been added as a bot admin!",
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(
                ctx.getEvent(),
                guildModel,
                "Added Mod Role",
                "Role added to admin " + role.get().getAsMention() +
                        " | " + role.get().getId()
        );
      }

      case "del" -> {
        if (!guildModel.getIds().getAdminRoles().contains(role.get().getId())) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() + " This role is not an admin role!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }

        guildModel.getIds().getAdminRoles().remove(role.get().getId());
        ctx.getClient().getDbManager().save(guildModel);

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " The " + role.get().getAsMention() +
                        " role has been removed from admin roles!",
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(
                ctx.getEvent(),
                guildModel,
                "Remove Mod Role",
                "Role removed from admin " + role.get().getAsMention() +
                        " | " + role.get().getId()
        );
      }
    }
  }
}
