package xyz.xenus.bot.commands.dev;

import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.user.UserModel;

import java.util.Optional;

public class Premium extends Command {
    public Premium() {
        super("premium");
        setCategory(Categories.DEV);
    }

    @Override
    public void run(CommandContext ctx) {
        if (
                !ctx.getUserModel().getBadges().isDeveloper() &&
                        !ctx.getEvent().getAuthor().getId().equals("543452691863437312")
        )
            return;

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
        UserModel userModel = ctx.getClient().getDbManager().find(member.getUser());
        userModel.getBadges().setPremium(!userModel.getBadges().isPremium());
        ctx.getUserModel().save();

        String msg = userModel.getBadges().isPremium() ?
                " has been granted premium membership!" :
                " has been removed from premium member ranks!";
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + member.getAsMention() + msg,
                Utils.Embeds.SUCCESS
        ).queue();
    }
}
