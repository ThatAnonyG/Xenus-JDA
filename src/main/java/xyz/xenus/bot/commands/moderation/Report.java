package xyz.xenus.bot.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.member.Actions;
import xyz.xenus.lib.mongodb.member.MemberModel;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Report extends Command {
    public Report() {
        super("report");
        setCd(10000);
        setCategory(Categories.MODERATION);
        setDescription("Submit a report against a player.");
        setUsage("<Mention User | ID> [Report]");
    }

    @Override
    public void run(CommandContext ctx) {
        TextChannel reportChannel = ctx.getEvent().getGuild().getTextChannelById(
                ctx.getGuildModel().getIds().getReports()
        );
        if (reportChannel == null) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Reporting other members is disabled in this server!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

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
        if (ctx.getEvent().getGuild().getSelfMember().equals(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot report myself!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (Objects.equals(ctx.getEvent().getMember(), member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You cannot report yourself!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (ctx.getArgs().isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Cannot report someone without reason!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        String report = String.join(" ", ctx.getArgs());
        if (report.length() > 1500) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Report cannot be more than 1500 letters!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        Actions memberAction = new Actions();
        memberAction.setMod(ctx.getEvent().getAuthor().getId());
        memberAction.setDate(System.currentTimeMillis());
        memberAction.setReason(report);

        MemberModel memberModel = ctx.getClient().getDbManager().init(member);
        memberModel.getReports().add(memberAction);
        memberModel.save();

        EmbedBuilder embed = Utils.embed()
                .setTitle("Report Against " + member.getUser().getAsTag())
                .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                .setColor(Utils.getHex())
                .addField("Reported Member", member.getAsMention() + " | " + member.getId(), false)
                .addField(
                        "Reported In",
                        ctx.getEvent().getChannel().getAsMention() + " | " + ctx.getEvent().getChannel().getId(),
                        false
                )
                .addField(
                        "Reported By",
                        ctx.getEvent().getAuthor().getAsMention() + " | " + ctx.getEvent().getAuthor().getId(),
                        false
                )
                .addField("Reason", report, false);
        reportChannel.sendMessage(embed.build()).queue();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " The report has been submitted!",
                Utils.Embeds.SUCCESS
        ).queue((m) -> m.delete().queueAfter(5000, TimeUnit.MILLISECONDS));
    }
}
