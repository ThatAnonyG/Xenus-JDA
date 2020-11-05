package xyz.xenus.bot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.member.Actions;
import xyz.xenus.lib.mongodb.member.MemberModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Reports extends Command {
    public Reports() {
        super("reports");
        setCategory(Categories.MODERATION);
        setDescription("View all the reports against an user.");
        setUsage("<Mention User | ID> [clear] - Remove all reports with 'clear'");
        setPerms(new Permission[]{Permission.MESSAGE_MANAGE});
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
        MemberModel memberModel = ctx.getClient().getDbManager().find(member);

        if (memberModel == null || memberModel.getReports().isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No reports found against that user!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        ArrayList<Actions> actions = memberModel.getReports();
        Arrays.sort(actions.toArray(Actions[]::new), (a, b) -> (int) (a.getDate() - b.getDate()));

        StringBuilder builder = new StringBuilder(
                "**" + "Reports against " + memberOptional.get().getEffectiveName() + "**\n```"
        );

        for (Actions action : actions) {
            Member mod = ctx.getEvent().getGuild().getMemberById(action.getMod());

            String add = "\nCase: #" + (actions.indexOf(action) + 1) + "| Date:" + Utils.formatDate(action.getDate()) +
                    "\nMod: " + (mod == null ? action.getMod() : mod.getUser().getAsTag()) +
                    "\n" + action.getReason();

            if (builder.toString().length() + add.length() > 1950) {
                ctx.getEvent().getChannel().sendMessage(builder.append("```").toString().trim()).queue();
                builder.delete(0, builder.length());
                builder.append("```").append(add);
                continue;
            }

            builder.append(add);
        }

        ctx.getEvent().getChannel().sendMessage(builder.append("```").toString().trim()).queue();
    }
}
