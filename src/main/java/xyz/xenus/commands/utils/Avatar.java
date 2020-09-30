package xyz.xenus.commands.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.util.Objects;

public class Avatar extends Command {
    public Avatar() {
        super("avatar");
        setAliases(new String[]{"pfp"});
        setCategory(Categories.UTILITY);
        setCd(2000);
        setDescription("Shows the profile picture of an user.");
        setUsage("<Mention User | ID>");
    }

    @Override
    public void run(CommandContext ctx) {
        Member member = Utils.getMember(ctx.getEvent().getMessage(), ctx.getArgs()).orElse(
                Objects.requireNonNull(ctx.getEvent().getMember())
        );

        ctx.getEvent().getChannel().sendMessage(
                new EmbedBuilder()
                        .setTitle(member.getEffectiveName() + "'s PFP")
                        .setColor(member.getColor() == null ? Utils.getHex() : member.getColor())
                        .setImage(member.getUser().getEffectiveAvatarUrl().concat("?size=1024"))
                        .build()
        ).queue();
    }
}
