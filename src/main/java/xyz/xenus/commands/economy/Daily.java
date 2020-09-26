package xyz.xenus.commands.economy;

import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.user.UserModel;

public class Daily extends Command {
  public Daily() {
    super("daily");
    setCategory(Categories.ECONOMY);
    setCd(10000);
    setDescription("Claim some coins as a part of daily rewards.");
    setUsage("[claim] - To claim your daily reward.");
  }

  @Override
  public void run(CommandContext ctx) {
    UserModel userModel =
            (UserModel) ctx.getClient().getDbManager().find(ctx.getEvent().getAuthor());
    long cd = userModel.getEconomy().getCd();

    short min = 200;
    short max = 500;
    long genCoins = (long) Math.floor(Math.random() * (max - min + 1) + min);

    if (ctx.hasSub(new String[]{"claim"}, 0)) {
      if (cd > System.currentTimeMillis()) {
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getCross() + " You can claim after " +
                        Utils.formatTime(cd - System.currentTimeMillis()) + "!",
                Utils.Embeds.ERROR
        ).queue();
        return;
      }

      if ((cd + 432e5) < System.currentTimeMillis()) {
        userModel.getEconomy().getDaily().setStreak(
                userModel.getEconomy().getDaily().getStreak() + 1
        );
      } else {
        userModel.getEconomy().getDaily().setStreak(1);
      }

      userModel.getEconomy().setCoins(userModel.getEconomy().getCoins() + genCoins);
      userModel.getEconomy().getDaily().setCd(
              (long) (System.currentTimeMillis() + 432e5)
      );
      ctx.getClient().getDbManager().save(userModel);

      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getTick() + " You claimed $" + genCoins + " for daily rewards!",
              Utils.Embeds.SUCCESS
      ).queue();
      return;
    }

    MessageEmbed embed = Utils.embed()
            .setTitle("Daily Rewards")
            .setDescription("Earn $200-500 using the `daily` command!")
            .addField(
                    "Your Streak",
                    String.valueOf(userModel.getEconomy().getDaily().getStreak()),
                    true
            )
            .addField(
                    "Next Claim",
                    cd < System.currentTimeMillis() ?
                            "Claim now to continue your streak!" :
                            "Claim in " + Utils.formatTime(cd - System.currentTimeMillis()),
                    true
            )
            .build();
    ctx.getEvent().getChannel().sendMessage(embed).queue();
  }
}
