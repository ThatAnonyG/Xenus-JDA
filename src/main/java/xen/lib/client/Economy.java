package xen.lib.client;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import xen.lib.Utils;
import xen.lib.mongodb.DBManager;
import xen.lib.mongodb.guild.GuildModel;
import xen.lib.mongodb.member.MemberModel;
import xen.lib.mongodb.user.UserModel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Economy {
  private final XenClient client;

  public Economy(XenClient client) {
    this.client = client;
  }

  public static @NotNull List<Member> sortXP(DBManager dbManager, @NotNull Guild guild) {
    Member[] sorted = guild.getMembers()
            .stream()
            .filter((m) -> dbManager.find(m) != null)
            .toArray(Member[]::new);

    Arrays.sort(
            sorted,
            (m1, m2) -> {
              long xp1 = ((MemberModel) dbManager.find(m1))
                      .getEconomy()
                      .getXp();
              long xp2 = ((MemberModel) dbManager.find(m2))
                      .getEconomy()
                      .getXp();

              return (int) (xp2 - xp1);
            }
    );

    return Arrays.asList(sorted);
  }

  public XenClient getClient() {
    return client;
  }

  public MemberModel genXP(
          GuildMessageReceivedEvent event, GuildModel guildDB, @NotNull MemberModel memberDB
  ) {
    memberDB.getEconomy().setCd(Calendar.getInstance().getTimeInMillis() + 60000);
    if (Math.random() * 100 < 50) return (MemberModel) client.getDbManager().save(memberDB);

    float xpRate = guildDB.getEconomy().getXpRate();
    byte xp = (byte) (xpRate % 1 == 0 ?
            Math.floor(Math.random() * 5 + 4) * xpRate :
            Math.round(Math.floor(Math.random() * 5 + 4) * xpRate));
    memberDB.getEconomy().setXp(memberDB.getEconomy().getXp() + xp);

    Guild guild = event.getGuild();
    Role xpLead = !guildDB.getEconomy().getXpLead().isEmpty() ?
            guild.getRoleById(guildDB.getEconomy().getXpLead()) :
            null;
    Member member = event.getMember();
    assert member != null;
    List<Member> xpSorted = sortXP(client.getDbManager(), guild);

    if (
            guild.getSelfMember().canInteract(member) &&
                    xpSorted.contains(member) &&
                    xpSorted.get(0) == member &&
                    xpLead != null &&
                    !member.getRoles().contains(xpLead)
    ) {
      guild.addRoleToMember(member, xpLead).queue();
      for (Member m : guild.getMembers().stream().filter(
              (m) -> m.getRoles().contains(xpLead)
      ).toArray(Member[]::new)
      ) {
        guild.removeRoleFromMember(m, xpLead).queue();
      }
    }

    if (memberDB.getEconomy().getXp() > memberDB.getEconomy().getNxtLvl()) {
      memberDB.getEconomy().setLevel(memberDB.getEconomy().getLevel() + 1);
      long nxtLvl = memberDB.getEconomy().getNxtLvl();
      memberDB.getEconomy().setNxtLvl(nxtLvl + Math.round(nxtLvl * 0.4));

      Utils.sendEm(
              event.getChannel(),
              "Level up, GG! You reached level `" + memberDB.getEconomy().getLevel() + "`!",
              Utils.Embeds.BASE
      ).queue((m) -> m.delete().queueAfter(3000, TimeUnit.MILLISECONDS));
    }

    return (MemberModel) client.getDbManager().save(memberDB);
  }

  public UserModel genCoin(
          GuildMessageReceivedEvent event, GuildModel guildDB, @NotNull UserModel userDB
  ) {
    userDB.getEconomy().setCd(Calendar.getInstance().getTimeInMillis() + 120000);
    if (Math.random() * 100 < 30) return (UserModel) client.getDbManager().save(userDB);

    byte coins = (byte) (Math.floor(Math.random() * 9) + 10);
    userDB.getEconomy().setCoins(userDB.getEconomy().getCoins() + coins);

    if (guildDB.getEconomy().isCoinAlert())
      Utils.sendEm(
              event.getChannel(),
              "GG **" + event.getAuthor().getName() + "**! You earned `" + coins + "` coins!",
              Utils.Embeds.BASE
      ).queue((m) -> m.delete().queueAfter(3000, TimeUnit.MILLISECONDS));

    return (UserModel) client.getDbManager().save(userDB);
  }
}
