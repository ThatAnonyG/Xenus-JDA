package xyz.xenus.commands.economy;

import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class Gamble extends Command {
    public Gamble() {
        super("gamble");
        setAliases(new String[]{"g"});
        setCategory(Categories.ECONOMY);
        setCd(300000);
        setDescription("Gamble your coins and try your luck.");
        setUsage("[Amount] - Leave blank to gamble all your coins");
    }

    @Override
    public void run(CommandContext ctx) {
        double random = Math.random() * 100;
        double profitBy = random < 40 ? 1.5 : random < 30 ? 2.0 : random < 15 ? 3.0 : 0;
        long bet = ctx.getArgs().isEmpty() || !Utils.isInteger(ctx.getArgs().get(0)) ?
                ctx.getUserModel().getEconomy().getCoins() :
                Integer.parseInt(ctx.getArgs().get(0));
        long addCoins = (long) ((bet * profitBy) - bet);

        if (profitBy == 0) {
            ctx.getUserModel().getEconomy().setCoins(0);
        } else {
            ctx.getUserModel().getEconomy().setCoins(ctx.getUserModel().getEconomy().getCoins() + addCoins);
        }

        ctx.getClient().getDbManager().save(ctx.getUserModel());
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                "You bet $" + bet + " and " +
                        (profitBy == 0 ? "lost it all!" : "earned $" + addCoins + "!"),
                Utils.Embeds.BASE
        ).queue();
    }
}
