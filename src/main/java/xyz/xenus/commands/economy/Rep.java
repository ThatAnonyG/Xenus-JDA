package xyz.xenus.commands.economy;

import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.user.UserModel;

import java.util.Optional;

public class Rep extends Command {
    public Rep() {
        super("rep");
        setCategory(Categories.ECONOMY);
        setCd(300000);
        setDescription("Give a user a rep point with a small review.");
        setUsage("<User Mention | ID> <pos | neg> <A Review>");
    }

    @Override
    public void run(CommandContext ctx) {
        if (ctx.getUserModel().getReps().getCd() > System.currentTimeMillis()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You can again rep someone after " +
                            Utils.formatTime(ctx.getUserModel().getReps().getCd() - System.currentTimeMillis()) +
                            "!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        Optional<Member> optionalMember = Utils.getMember(ctx.getEvent().getMessage(), ctx.getArgs());
        if (optionalMember.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No user found with the given with!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        ctx.getArgs().remove(0);

        if (!ctx.hasSub(new String[]{"pos", "neg"}, 0)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() +
                            " Use a proper rep type - `pos` for positive and `neg` for negative!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        String type = ctx.getArgs().remove(0).toLowerCase();
        if (ctx.getArgs().isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() +
                            " Please provide a review for the user!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        UserModel userModel = (UserModel) ctx.getClient().getDbManager().init(optionalMember.get().getUser());
        if (type.equals("pos")) {
            userModel.getReps().getPos().add(
                    "*" + ctx.getEvent().getAuthor().getName() + "*: " + String.join(" ", ctx.getArgs())
            );
        } else {
            userModel.getReps().getNeg().add(
                    "*" + ctx.getEvent().getAuthor().getName() + "*: " + String.join(" ", ctx.getArgs())
            );
        }

        ctx.getUserModel().getReps().setCd(System.currentTimeMillis() + 86400000);
        ctx.getClient().getDbManager().save(userModel);
        ctx.getClient().getDbManager().save(ctx.getUserModel());
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() +
                        " Rep submitted for user " + optionalMember.get().getAsMention() + "!",
                Utils.Embeds.SUCCESS
        ).queue();
    }
}
