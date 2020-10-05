package xyz.xenus.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.util.Objects;
import java.util.Optional;

public class Role extends Command {
    public Role() {
        super("role");
        setCategory(Categories.MODERATION);
        setDescription("Add or remove a role from an user.");
        setUsage("<Mention User | ID> [add | del] <Role Mention | ID>");
        setPerms(new Permission[]{Permission.MANAGE_ROLES});
        setBotPerms(new Permission[]{Permission.MANAGE_ROLES});
    }

    @Override
    public void run(CommandContext ctx) {
        Optional<Member> memberOptional = Utils.getMember(ctx.getEvent().getMessage(), ctx.getArgs());
        if (memberOptional.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No user found with the given info!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        Member member = memberOptional.get();
        ctx.getArgs().remove(0);
        if (!Objects.requireNonNull(ctx.getEvent().getMember()).canInteract(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() +
                            " You cannot edit their roles them because they are on a higher position than you!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (!ctx.getEvent().getGuild().getSelfMember().canInteract(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot edit their roles because they are mod/admin!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (ctx.getEvent().getGuild().getSelfMember().equals(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot edit my roles!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (Objects.equals(ctx.getEvent().getMember(), member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You cannot edit your roles!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        if (!ctx.hasSub(new String[]{"add", "del"}, 0)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() +
                            " Please specify the type - `add` for Adding `del` for Removing!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        String sub = ctx.getArgs().remove(0).toLowerCase();

        Optional<net.dv8tion.jda.api.entities.Role> roleOptional = Utils.getRole(
                ctx.getEvent().getMessage(), ctx.getArgs()
        );
        if (roleOptional.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No role found with the given info!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        if (ctx.getArgs().get(0).toLowerCase().equals("add")) {
            if (member.getRoles().contains(roleOptional.get())) {
                Utils.sendEm(
                        ctx.getEvent().getChannel(),
                        ctx.getClient().getCross() + " This user already possesses that role!",
                        Utils.Embeds.ERROR
                ).queue();
                return;
            }
            ctx.getEvent().getGuild().addRoleToMember(member, roleOptional.get()).queue();
        } else {
            if (!member.getRoles().contains(roleOptional.get())) {
                Utils.sendEm(
                        ctx.getEvent().getChannel(),
                        ctx.getClient().getCross() + " This user does not possess that role!",
                        Utils.Embeds.ERROR
                ).queue();
                return;
            }
            ctx.getEvent().getGuild().removeRoleFromMember(member, roleOptional.get()).queue();
        }

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " Roles changed for user ***" + member.getUser().getAsTag() +
                        "***. " + (sub.equals("add") ? "Added " : "Removed ") + roleOptional.get().getAsMention() + "!",
                Utils.Embeds.ERROR
        ).queue();
    }
}
