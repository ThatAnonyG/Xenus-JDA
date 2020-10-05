package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.util.Optional;

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
        if (ctx.getArgs().isEmpty()) {
            ctx.getGuildModel().getWelcome().setRole("");
            ctx.getGuildModel().save();
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getTick() + " Auto role has been disabled!",
                    Utils.Embeds.SUCCESS
            ).queue();
            return;
        }

        Optional<Role> role = Utils.getRole(ctx.getEvent().getMessage(), ctx.getArgs());
        if (role.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No role found with the given info!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (ctx.getGuildModel().getWelcome().getRole().equals(role.get().getId())) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " This role is already set as default role!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        ctx.getGuildModel().getWelcome().setRole(role.get().getId());
        ctx.getGuildModel().save();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " The " + role.get().getAsMention() +
                        " role has been set as default role!",
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(
                ctx.getEvent(),
                ctx.getGuildModel(),
                "Changed Default Role",
                "Default role changed to " + role.get().getAsMention() +
                        " | " + role.get().getId()
        );
    }
}
