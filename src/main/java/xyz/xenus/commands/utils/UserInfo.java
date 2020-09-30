package xyz.xenus.commands.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.sql.Date;
import java.util.Objects;

public class UserInfo extends Command {
    public UserInfo() {
        super("userInfo");
        setAliases(new String[]{"whoIs"});
        setCategory(Categories.UTILITY);
        setCd(5000);
        setDescription("Shows a user's account information.");
        setUsage("[User Mention | ID]");
    }

    @Override
    public void run(CommandContext ctx) {
        Member member = Utils.getMember(ctx.getEvent().getMessage(), ctx.getArgs()).orElse(
                Objects.requireNonNull(ctx.getEvent().getMember())
        );

        EmbedBuilder embed = Utils.embed()
                .setTitle("Info For " + member.getUser().getAsTag())
                .setColor(member.getColor() == null ? Utils.getHex() : member.getColor())
                .addField("User", member.getAsMention(), true)
                .addField("User ID", member.getUser().getId(), true)
                .addField("Nickname", member.getEffectiveName(), true)
                .addField(
                        "Joined Discord",
                        Date.from(member.getUser().getTimeCreated().toInstant()).toString(),
                        true
                );

        if (member.hasTimeJoined()) embed.addField(
                "Joined Server",
                Date.from(member.getTimeJoined().toInstant()).toString(),
                true
        );
        embed.addField(
                "Roles",
                String.join(
                        " | ",
                        member.getRoles().stream().map(IMentionable::getAsMention).toArray(String[]::new)
                ),
                false
        );

        ctx.getEvent().getChannel().sendMessage(embed.build()).queue();
    }
}
