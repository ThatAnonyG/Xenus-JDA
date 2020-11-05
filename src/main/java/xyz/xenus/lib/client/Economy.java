package xyz.xenus.lib.client;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.mongodb.DBManager;
import xyz.xenus.lib.mongodb.Model;
import xyz.xenus.lib.mongodb.guild.GuildModel;
import xyz.xenus.lib.mongodb.member.MemberModel;
import xyz.xenus.lib.mongodb.user.UserModel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Economy {
    private final XenClient client;

    public Economy(XenClient client) {
        this.client = client;
    }

    public static @NotNull
    List<String> sortXP(DBManager dbManager, @NotNull Guild guild) {
        Document query = new Document().append(
                "model_id",
                new Document().append("$regex", ".*\\d," + guild.getId() + "$").append("$options", "gi")
        );
        Model[] sorted = dbManager.findMany(query, Model.ModelType.MEMBER).toArray(Model[]::new);

        Arrays.sort(sorted, (m1, m2) -> {
            long xp1 = ((MemberModel) m1).getEconomy().getXp();
            long xp2 = ((MemberModel) m2).getEconomy().getXp();
            return (int) (xp2 - xp1);
        });

        return Arrays.asList(Arrays.stream(sorted).map(
                (m) -> m.getModelId().split(",")[0]
        ).toArray(String[]::new));
    }

    public XenClient getClient() {
        return client;
    }

    public MemberModel genXP(
            GuildMessageReceivedEvent event, GuildModel guildDB, @NotNull MemberModel memberDB
    ) {
        memberDB.getEconomy().setCd(System.currentTimeMillis() + 60000);
        if (Math.random() * 100 < 50) return memberDB.save();

        float xpRate = guildDB.getEconomy().getXpRate();
        byte max = 10;
        byte min = 4;

        byte xp = (byte) (xpRate % 1 == 0 ?
                Math.floor(Math.random() * (max - min + 1) + min) * xpRate :
                Math.round(Math.floor(Math.random() * (max - min + 1) + min) * xpRate));
        memberDB.getEconomy().setXp(memberDB.getEconomy().getXp() + xp);

        Guild guild = event.getGuild();
        Role xpLead = !guildDB.getEconomy().getXpLead().isEmpty() ?
                guild.getRoleById(guildDB.getEconomy().getXpLead()) :
                null;
        Member member = event.getMember();
        assert member != null;
        List<String> xpSorted = sortXP(client.getDbManager(), guild);

        if (
                guild.getSelfMember().canInteract(member) &&
                        xpSorted.contains(member.getId()) &&
                        xpSorted.get(0).equals(member.getId()) &&
                        xpLead != null &&
                        !member.getRoles().contains(xpLead)
        ) {
            guild.addRoleToMember(member, xpLead).queue();
            guild.findMembers(
                    (m) -> m.getRoles().contains(xpLead) && guild.getSelfMember().canInteract(m)
            ).onSuccess((memberList) -> {
                for (Member m : memberList) guild.removeRoleFromMember(m, xpLead).queue();
            });
        }

        if (memberDB.getEconomy().getXp() > memberDB.getEconomy().getNxtLvl()) {
            memberDB.getEconomy().setLevel(memberDB.getEconomy().getLevel() + 1);
            long nxtLvl = memberDB.getEconomy().getNxtLvl();
            memberDB.getEconomy().setNxtLvl(nxtLvl + Math.round(nxtLvl * 0.4));

            if (guildDB.getEconomy().isLvlUpAlert())
                Utils.sendEm(
                        event.getChannel(),
                        "Level up, GG! You reached level `" + memberDB.getEconomy().getLevel() + "`!",
                        Utils.Embeds.BASE
                ).queue((m) -> m.delete().queueAfter(3000, TimeUnit.MILLISECONDS));
        }

        return memberDB.save();
    }

    public UserModel genCoin(
            GuildMessageReceivedEvent event, GuildModel guildDB, @NotNull UserModel userDB
    ) {
        userDB.getEconomy().setCd(System.currentTimeMillis() + 120000);
        if (Math.random() * 100 > 30) return userDB.save();

        byte max = 20;
        byte min = 10;
        byte coins = (byte) Math.floor(Math.random() * (max - min + 1) + min);
        userDB.getEconomy().setCoins(userDB.getEconomy().getCoins() + coins);

        if (guildDB.getEconomy().isCoinAlert())
            Utils.sendEm(
                    event.getChannel(),
                    "GG **" + event.getAuthor().getName() + "**! You earned `" + coins + "` coins!",
                    Utils.Embeds.BASE
            ).queue((m) -> m.delete().queueAfter(3000, TimeUnit.MILLISECONDS));

        return userDB.save();
    }
}
