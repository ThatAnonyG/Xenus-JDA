package xyz.xenus.commands.economy;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.client.Economy;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.member.MemberModel;

import java.util.List;
import java.util.Objects;

public class Leaderboard extends Command {
    public Leaderboard() {
        super("leaderboard");
        setAliases(new String[]{"lb"});
        setCategory(Categories.ECONOMY);
        setCd(5000);
        setDescription("Shows the top 10 members of the server based on XP.");
    }

    @Override
    public void run(CommandContext ctx) {
        if (!ctx.getGuildModel().getEnabled().contains("xp")) return;

        List<String> sorted = Economy.sortXP(ctx.getClient().getDbManager(), ctx.getEvent().getGuild());
        StringBuilder list = new StringBuilder();

        for (byte i = 0; i < sorted.size(); i++) {
            Member member = ctx.getEvent().getGuild().getMemberById(sorted.get(i));
            if (member == null) continue;
            MemberModel db = (MemberModel) ctx.getClient().getDbManager().find(member);
            list.append("[**").append(i + 1).append("**] ").append(member.getUser().getName())
                    .append(" | XP: ").append(db.getEconomy().getXp()).append("\n");
        }

        String id = Objects.requireNonNull(ctx.getEvent().getMember()).getId();
        MessageEmbed embed = Utils.embed()
                .setTitle(ctx.getEvent().getGuild().getName() + " Leaderboard [Top 10]")
                .setColor(Utils.getHex())
                .setDescription(list.toString())
                .addField(
                        "Your Position",
                        sorted.contains(id) ? "You are at the **#" + (sorted.indexOf(id) + 1) +
                                "** position!" :
                                "Not Ranked!",
                        false
                )
                .build();
        ctx.getEvent().getChannel().sendMessage(embed).queue();
    }
}
