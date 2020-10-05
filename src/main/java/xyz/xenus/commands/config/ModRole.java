package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.util.Optional;

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

        Optional<Role> role = Utils.getRole(ctx.getEvent().getMessage(), ctx.getArgs());
        if (role.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No role found with the given info!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        switch (sub) {
            case "add" -> {
                if (ctx.getGuildModel().getIds().getAdminRoles().contains(role.get().getId())) {
                    Utils.sendEm(
                            ctx.getEvent().getChannel(),
                            ctx.getClient().getCross() + " This role is already an admin role!",
                            Utils.Embeds.ERROR
                    ).queue();
                    return;
                }

                ctx.getGuildModel().getIds().getAdminRoles().add(role.get().getId());
                ctx.getGuildModel().save();

                Utils.sendEm(
                        ctx.getEvent().getChannel(),
                        ctx.getClient().getTick() + " The " + role.get().getAsMention() +
                                " role has been added as a bot admin!",
                        Utils.Embeds.SUCCESS
                ).queue();
                Utils.sendConfigLog(
                        ctx.getEvent(),
                        ctx.getGuildModel(),
                        "Added Mod Role",
                        "Role added to admin " + role.get().getAsMention() +
                                " | " + role.get().getId()
                );
            }

            case "del" -> {
                if (!ctx.getGuildModel().getIds().getAdminRoles().contains(role.get().getId())) {
                    Utils.sendEm(
                            ctx.getEvent().getChannel(),
                            ctx.getClient().getCross() + " This role is not an admin role!",
                            Utils.Embeds.ERROR
                    ).queue();
                    return;
                }

                ctx.getGuildModel().getIds().getAdminRoles().remove(role.get().getId());
                ctx.getGuildModel().save();

                Utils.sendEm(
                        ctx.getEvent().getChannel(),
                        ctx.getClient().getTick() + " The " + role.get().getAsMention() +
                                " role has been removed from admin roles!",
                        Utils.Embeds.SUCCESS
                ).queue();
                Utils.sendConfigLog(
                        ctx.getEvent(),
                        ctx.getGuildModel(),
                        "Remove Mod Role",
                        "Role removed from admin " + role.get().getAsMention() +
                                " | " + role.get().getId()
                );
            }
        }
    }
}
