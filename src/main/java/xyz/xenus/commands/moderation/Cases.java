package xyz.xenus.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.Actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Cases extends Command {
    public Cases() {
        super("cases");
        setCategory(Categories.MODERATION);
        setDescription("See moderation cases against a user.");
        setUsage("<Mention User | ID>");
        setPerms(new Permission[]{Permission.MESSAGE_MANAGE});
    }

    @Override
    public void run(CommandContext ctx) {
        Optional<Member> memberOptional = Utils.getMember(ctx.getEvent().getMessage(), ctx.getArgs());
        ArrayList<Actions> actions = new ArrayList<>();

        ctx.getGuildModel().getLogs().values().forEach(
                (logs) -> {
                    if (memberOptional.isPresent()) {
                        Actions[] actionArr = logs.getActions().stream().filter(
                                (x) -> x.getUser().equals(memberOptional.get().getId())
                        ).toArray(Actions[]::new);

                        for (Actions x : actionArr) {
                            x.setReason("[" + logs.getType().name() + "] " + x.getReason());
                            actions.add(x);
                        }
                    } else {
                        for (Actions x : logs.getActions()) {
                            x.setReason("[" + logs.getType().name() + "] " + x.getReason());
                            actions.add(x);
                        }
                    }
                }
        );
        if (actions.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No logs found against that user!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        Arrays.sort(actions.toArray(Actions[]::new), (a, b) -> (int) (a.getDate() - b.getDate()));

        StringBuilder builder = new StringBuilder(
                "**" + (
                        memberOptional.isEmpty() ?
                                "Server Cases" :
                                "Cases for " + memberOptional.get().getEffectiveName()
                ) + "**\n```"
        );

        for (Actions action : actions) {
            Member mod = ctx.getEvent().getGuild().getMemberById(action.getMod());
            Member mem = ctx.getEvent().getGuild().retrieveMemberById(action.getUser()).complete();

            String add = "\nCase: #" + (actions.indexOf(action) + 1) + "| Date:" + Utils.formatDate(action.getDate()) +
                    "\nMod: " + (mod == null ? action.getMod() : mod.getUser().getAsTag()) +
                    "\nMember: " + (mem == null ? action.getUser() : mem.getUser().getAsTag()) +
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
