package xyz.xenus.bot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.Logs;
import xyz.xenus.lib.mongodb.member.Actions;
import xyz.xenus.lib.mongodb.member.MemberModel;

import java.util.Objects;
import java.util.Optional;

public class Warn extends Command {
    public Warn() {
        super("warn");
        setCategory(Categories.MODERATION);
        setDescription("Warn a user with a reason.");
        setUsage("<Mention User | ID> <Reason>");
        setPerms(new Permission[]{Permission.MESSAGE_MANAGE});
        setBotPerms(new Permission[]{Permission.MESSAGE_MANAGE});
    }

    @Override
    public void run(CommandContext ctx) {
        Optional<Member> optionalMember = Utils.getMember(ctx.getEvent().getMessage(), ctx.getArgs());
        if (optionalMember.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No user found with the given info!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        Member member = optionalMember.get();
        ctx.getArgs().remove(0);
        if (!Objects.requireNonNull(ctx.getEvent().getMember()).canInteract(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() +
                            " You cannot warn them because they are on a higher position than you!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (ctx.getEvent().getGuild().getSelfMember().equals(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot warn myself!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (Objects.equals(ctx.getEvent().getMember(), member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You cannot warn yourself!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        String reason = String.join(" ", ctx.getArgs());
        if (reason.length() == 0) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You cannot warn without reason!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        member.getUser().openPrivateChannel().queue(
                (c) -> Utils.sendEm(
                        c,
                        "You were warned in " + ctx.getEvent().getGuild().getName() + "!\n**Reason:** " + reason,
                        Utils.Embeds.BASE
                ).queue()
        );

        Actions memberAction = new Actions();
        memberAction.setMod(ctx.getEvent().getAuthor().getId());
        memberAction.setDate(System.currentTimeMillis());
        memberAction.setReason(reason);

        MemberModel memberModel = ctx.getClient().getDbManager().init(member);
        memberModel.getWarns().add(memberAction);
        memberModel.save();

        if (ctx.getGuildModel().getLogs().get(Logs.LogTypes.WARN.name()).isEnabled()) {
            xyz.xenus.lib.mongodb.guild.Actions guildAction = new xyz.xenus.lib.mongodb.guild.Actions();
            guildAction.setMod(ctx.getEvent().getAuthor().getId());
            guildAction.setUser(member.getId());
            guildAction.setDate(System.currentTimeMillis());
            guildAction.setReason(reason);

            ctx.getGuildModel().getLogs().get(Logs.LogTypes.WARN.name()).getActions().add(guildAction);
            ctx.getGuildModel().save();
        }

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " ***" + member.getUser().getAsTag() + "*** has been warned!" +
                        "\n**Reason:** " + reason,
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendModLog(ctx.getEvent(), ctx.getGuildModel(), "User Warned", reason, member.getUser());
    }
}
