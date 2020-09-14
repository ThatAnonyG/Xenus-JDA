package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import xen.lib.Utils;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;

import java.util.concurrent.atomic.AtomicReference;

public class TopRole extends Command {
  private GuildModel guildModel;

  public TopRole() {
    super("topRole");
    setCategory(Categories.CONFIG);
    setDescription("Let's you set a role for the user with most XP.");
    setUsage("[Role Mention | ID] - Leave it blank to disable XP lead role");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    guildModel = (GuildModel) ctx.getClient().getDbManager().find(ctx.getEvent().getGuild());

    if (ctx.getArgs().isEmpty()) {
      guildModel.getEconomy().setXpLead("");
      ctx.getClient().getDbManager().save(guildModel);
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getTick() + " XP lead role has been disabled!",
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
    if (guildModel.getEconomy().getXpLead().equals(role.get().getId())) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() + " This role is already set as XP lead role!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    guildModel.getEconomy().setXpLead(role.get().getId());
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
            "Changed XP Lead Role",
            "XP lead role changed to " + role.get().getAsMention() +
                    " | " + role.get().getId()
    );
  }
}
