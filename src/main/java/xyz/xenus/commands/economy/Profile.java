package xyz.xenus.commands.economy;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.client.Economy;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.member.MemberModel;
import xyz.xenus.lib.mongodb.user.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO - Fix the getMember function

public class Profile extends Command {
  public Profile() {
    super("profile");
    setCategory(Categories.ECONOMY);
    setCd(2000);
    setDescription("See yours or some other user's bot profile.");
    setUsage("<Mention User | ID>\n" +
            "<bio> [New Bio] - Change profile bio. Leave new bio blank to reset your bio.");
  }

  @Override
  public void run(CommandContext ctx) {
    if (ctx.hasSub(new String[]{"bio"}, 0)) {
      ctx.getArgs().remove(0);
      UserModel userModel = (UserModel) ctx.getClient().getDbManager().find(
              ctx.getEvent().getAuthor()
      );
      String newBio = String.join(" ", ctx.getArgs());

      userModel.setBio(newBio.length() == 0 ? "Not set!" : newBio);
      ctx.getClient().getDbManager().save(userModel);
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getTick() + " Changed and saved new bio!",
              Utils.Embeds.SUCCESS
      ).queue();
      return;
    }

    Member member = Utils.getMember(ctx.getEvent().getMessage(), ctx.getArgs()).orElse(
            Objects.requireNonNull(ctx.getEvent().getMember())
    );

    UserModel userModel = (UserModel) ctx.getClient().getDbManager().find(member.getUser());
    MemberModel memberModel = (MemberModel) ctx.getClient().getDbManager().find(member);

    List<String> serverXP = Economy.sortXP(
            ctx.getClient().getDbManager(), ctx.getEvent().getGuild()
    );
    String level = (memberModel != null ? memberModel.getEconomy().getLevel() : 0) +
            " (**Rank:** " + (serverXP.contains(member.getId()) ?
            serverXP.indexOf(member.getId()) + 1 :
            0) + ")";
    long nextLvl = memberModel == null ? 500 : memberModel.getEconomy().getNxtLvl();
    String xp = (memberModel == null ? 0 : memberModel.getEconomy().getXp()) +
            " (**To Level Up:** " +
            (memberModel == null ?
                    "0/500" :
                    nextLvl == 500 ?
                            memberModel.getEconomy().getXp() + "/500" :
                            (memberModel.getEconomy().getXp() - Math.round(nextLvl / 1.4)) + "/" +
                                    (nextLvl - Math.round(nextLvl / 1.4))) +
            ")";

    ArrayList<String> badges = new ArrayList<>();
    if (userModel != null && userModel.getBadges().isPremium()) badges.add("`Premium`");
    if (userModel != null && userModel.getBadges().isDeveloper()) badges.add("`Developer`");

    MessageEmbed embed = Utils.embed()
            .setTitle(
                    userModel != null && userModel.getBadges().isBlacklisted() ?
                            "[BLACKLISTED] " :
                            "" + "Profile of " + member.getUser().getName()
            )
            .setColor(
                    userModel == null ?
                            Utils.getHex() :
                            userModel.getBadges().isBlacklisted() ?
                                    Utils.getRed() :
                                    userModel.getBadges().isPremium() ?
                                            Utils.getProHex() :
                                            Utils.getHex()
            )
            .setThumbnail(member.getUser().getAvatarUrl())
            .addField(
                    "Server Stats",
                    String.join(
                            "\n",
                            "**Nickname:** " + member.getEffectiveName(),
                            "**Level:** " + level,
                            "**XP:** " + xp
                    ),
                    false
            )
            .addField(
                    "Global Stats",
                    String.join(
                            "\n",
                            "**Badges:** " +
                                    (badges.isEmpty() ? "None" : String.join(" | ", badges)),
                            "**Money:** $" +
                                    (userModel == null ? 0 : userModel.getEconomy().getCoins()),
                            "**Positive Reps:** " +
                                    (userModel == null ? 0 : userModel.getReps().getPos().size()),
                            "**Negative Reps:** " +
                                    (userModel == null ? 0 : userModel.getReps().getNeg().size())
                    ),
                    false
            )
            .addField("User Bio", userModel == null ? "No bio set" : userModel.getBio(), false)
            .build();

    ctx.getEvent().getChannel().sendMessage(embed).queue();
  }
}
