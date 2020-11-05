package xyz.xenus.bot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.Actions;
import xyz.xenus.lib.mongodb.guild.Logs;
import xyz.xenus.lib.mongodb.member.MemberModel;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class Unmute extends Command {
    public Unmute() {
        super("unmute");
        setCategory(Categories.MODERATION);
        setDescription("Unmute a muted user in the server.");
        setUsage("<Mention User | ID> [Reason]");
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
                            " You cannot unmute them because they are on a higher position than you!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (!ctx.getEvent().getGuild().getSelfMember().canInteract(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot unmute them because they are mod/admin!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (ctx.getEvent().getGuild().getSelfMember().equals(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I am not muted!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (Objects.equals(ctx.getEvent().getMember(), member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You are not muted!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        String reason = ctx.getArgs().isEmpty() ? "No reason provided!" : String.join(" ", ctx.getArgs());
        if (reason.length() > 300) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Reason cannot be more than 300 letters!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        Optional<Role> optionalRole = Utils.getRole(ctx.getEvent().getMessage(), Collections.singletonList("muted"));
        if (optionalRole.isEmpty() || member.getRoles().contains(optionalRole.get())) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " This member not muted!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        ctx.getEvent().getGuild().addRoleToMember(member, optionalRole.get()).queue();
        member.getUser().openPrivateChannel().queue(
                (c) -> Utils.sendEm(
                        c,
                        "You have been unmuted in " + ctx.getEvent().getGuild().getName() + "!\n**Reason:** " +
                                reason,
                        Utils.Embeds.BASE
                ).queue()
        );

        if (ctx.getGuildModel().getLogs().get(Logs.LogTypes.UNMUTE.name()).isEnabled()) {
            Actions action = new Actions();
            action.setMod(ctx.getEvent().getAuthor().getId());
            action.setUser(member.getId());
            action.setDate(System.currentTimeMillis());
            action.setReason(reason);

            ctx.getGuildModel().getLogs().get(Logs.LogTypes.UNMUTE.name()).getActions().add(action);
            ctx.getGuildModel().save();
        }

        MemberModel memberModel = ctx.getClient().getDbManager().find(member);
        memberModel.setMuted(false);
        memberModel.save();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " ***" + member.getUser().getAsTag() +
                        "*** has been unmuted in the server!" + "\n**Reason:** " + reason,
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendModLog(ctx.getEvent(), ctx.getGuildModel(), "User Unmuted", reason, member.getUser());
    }
}
