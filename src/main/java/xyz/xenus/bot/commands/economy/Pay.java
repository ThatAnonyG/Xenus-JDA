package xyz.xenus.bot.commands.economy;

import net.dv8tion.jda.api.entities.Member;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.user.UserModel;

import java.util.Optional;

public class Pay extends Command {
    public Pay() {
        super("pay");
        setCategory(Categories.ECONOMY);
        setCd(120000);
        setDescription("Pay someone coins from your account.");
        setUsage("<Mention User | ID> <Amount>");
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
        UserModel userModel = ctx.getClient().getDbManager().find(member.getUser());

        if (ctx.getArgs().size() < 2 || !Utils.isInteger(ctx.getArgs().get(1))) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Please enter an amount your want to pay!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        long amount = Integer.parseInt(ctx.getArgs().get(1));
        if (amount > ctx.getUserModel().getEconomy().getCoins()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Amount cannot be more than your balance!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        ctx.getUserModel().getEconomy().setCoins(ctx.getUserModel().getEconomy().getCoins() - amount);
        userModel.getEconomy().setCoins(userModel.getEconomy().getCoins() + amount);
        ctx.getUserModel().save();
        userModel.save();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " Paid $" + amount + " to " +
                        member.getAsMention() + "!",
                Utils.Embeds.SUCCESS
        ).queue();
    }
}
