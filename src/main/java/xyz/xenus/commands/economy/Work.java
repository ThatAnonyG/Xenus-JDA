package xyz.xenus.commands.economy;

import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class Work extends Command {
    public Work() {
        super("work");
        setCategory(Categories.ECONOMY);
        setCd(30000);
        setDescription("Work daily to earn some money.");
    }

    @Override
    public void run(CommandContext ctx) {
        if (ctx.getUserModel().getEconomy().getJob().getName() == null) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You don't have a job yet. Use the `job` command to get one!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (ctx.getUserModel().getEconomy().getJob().getReady() > System.currentTimeMillis()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You can work again after " + Utils.formatTime(
                            ctx.getUserModel().getEconomy().getJob().getReady() - System.currentTimeMillis()
                    ) + "!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        long salary = ctx.getUserModel().getEconomy().getJob().getSalary();
        ctx.getUserModel().getEconomy().getJob().setReady(System.currentTimeMillis() + 86400000);
        ctx.getUserModel().getEconomy().setCoins(ctx.getUserModel().getEconomy().getCoins() + salary);
        ctx.getClient().getDbManager().save(ctx.getUserModel());

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " You worked as a " +
                        ctx.getUserModel().getEconomy().getJob().getName() + " and earned $" + salary + "!",
                Utils.Embeds.SUCCESS
        ).queue();
    }
}
