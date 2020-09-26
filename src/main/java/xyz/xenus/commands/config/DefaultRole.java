package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;

import java.util.concurrent.atomic.AtomicReference;

public class DefaultRole extends Command {
  public DefaultRole() {
    super("defRole");
    setCategory(Categories.CONFIG);
    setDescription("Let's you set a role for new users.");
    setUsage("[Role Mention | ID] - Leave it blank to disable auto role");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
    setBotPerms(new Permission[]{Permission.MANAGE_ROLES});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    GuildModel guildModel = (GuildModel) ctx.getClient().getDbManager().find(
            ctx.getEvent().getGuild()
    );

    if (ctx.getArgs().isEmpty()) {
      guildModel.getWelcome().setRole("");
      ctx.getClient().getDbManager().save(guildModel);
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getTick() + " Auto role has been disabled!",
              Utils.Embeds.SUCCESS
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
    if (guildModel.getWelcome().getRole().equals(role.get().getId())) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() + " This role is already set as default role!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    guildModel.getWelcome().setRole(role.get().getId());
    ctx.getClient().getDbManager().save(guildModel);

    Utils.sendEm(
            ctx.getEvent().getChannel(),
            ctx.getClient().getTick() + " The " + role.get().getAsMention() +
                    " role has been set as default role!",
            Utils.Embeds.SUCCESS
    ).queue();
    Utils.sendConfigLog(
            ctx.getEvent(),
            guildModel,
            "Changed Default Role",
            "Default role changed to " + role.get().getAsMention() +
                    " | " + role.get().getId()
    );
  }
}
