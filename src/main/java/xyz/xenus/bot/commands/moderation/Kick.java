package xyz.xenus.bot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.Actions;
import xyz.xenus.lib.mongodb.guild.Logs;

import java.util.Objects;
import java.util.Optional;

public class Kick extends Command {
    public Kick() {
        super("kick");
        setCategory(Categories.MODERATION);
        setDescription("Kick a user from the server.");
        setUsage("<Mention User | ID> [Reason]");
        setPerms(new Permission[]{Permission.KICK_MEMBERS});
        setBotPerms(new Permission[]{Permission.KICK_MEMBERS});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
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
                            " You cannot kick them because they are on a higher position than you!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (!ctx.getEvent().getGuild().getSelfMember().canInteract(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot kick them because they are mod/admin!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (ctx.getEvent().getGuild().getSelfMember().equals(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot kick myself!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (Objects.equals(ctx.getEvent().getMember(), member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You cannot kick yourself!",
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

        member.getUser().openPrivateChannel().queue(
                (c) -> Utils.sendEm(
                        c,
                        "You have been kicked from " + ctx.getEvent().getGuild().getName() + "!\n**Reason:** " +
                                reason,
                        Utils.Embeds.BASE
                ).queue()
        );
        ctx.getEvent().getGuild().kick(member, reason).queue();

        if (ctx.getGuildModel().getLogs().get(Logs.LogTypes.KICK.name()).isEnabled()) {
            Actions action = new Actions();
            action.setMod(ctx.getEvent().getAuthor().getId());
            action.setUser(member.getId());
            action.setDate(System.currentTimeMillis());
            action.setReason(reason);

            ctx.getGuildModel().getLogs().get(Logs.LogTypes.KICK.name()).getActions().add(action);
            ctx.getGuildModel().save();
        }

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " ***" + member.getUser().getAsTag() +
                        "*** has been kicked from the server!" + "\n**Reason:** " + reason,
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendModLog(ctx.getEvent(), ctx.getGuildModel(), "User Kicked", reason, member.getUser());
    }
}
