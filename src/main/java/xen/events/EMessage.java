package xen.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import xen.lib.client.Economy;
import xen.lib.client.XenClient;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;
import xen.lib.mongodb.member.CD;
import xen.lib.mongodb.member.MemberModel;
import xen.lib.mongodb.user.UserModel;
import xen.lib.utils.Utils;

import java.util.*;
import java.util.regex.Pattern;

import static xen.lib.utils.Utils.sendEm;

public class EMessage implements BaseEvent {
  private final XenClient client;
  private final String name;
  private GuildModel guildDB;
  private UserModel userDB;
  private MemberModel memberDB;

  public EMessage(XenClient client) {
    this.client = client;
    this.name = "GuildMessageReceivedEvent";
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public XenClient getClient() {
    return client;
  }

  @Override
  public void handle(@NotNull GenericEvent rawEvent) {
    GuildMessageReceivedEvent event = ((GuildMessageReceivedEvent) rawEvent);
    Message message = event.getMessage();
    if (event.getAuthor().isBot()) return;
    if (event.isWebhookMessage()) return;

    client.getDbManager().init(event.getGuild());
    client.getDbManager().init(event.getAuthor());
    client.getDbManager().init(Objects.requireNonNull(event.getMember()));

    guildDB = (GuildModel) client.getDbManager().find(event.getGuild());
    userDB = (UserModel) client.getDbManager().find(event.getAuthor());
    memberDB = (MemberModel) client.getDbManager().find(event.getMember());

    Economy ecoModule = new Economy(event, client);
    if (
            memberDB.getEconomy().getCd() < Calendar.getInstance().getTimeInMillis() &&
                    guildDB.getEnabled().contains("xp") &&
                    !guildDB.getEconomy().getBlocked().contains(message.getChannel().getId())
    )
      memberDB = ecoModule.genXP(guildDB, memberDB);
    if (userDB.getEconomy().getCd() < Calendar.getInstance().getTimeInMillis())
      userDB = ecoModule.genCoin(guildDB, userDB);

    if (message.getContentRaw().matches("^<@!?" +
            event.getJDA().getSelfUser().getId() +
            ">( help)?$")
    ) {
      event
              .getChannel()
              .sendMessage("**Use `" + guildDB.getPrefix() + "help` for more help!**")
              .queue();
      return;
    }

    ArrayList<String> args = new ArrayList<>(
            Arrays.asList(
                    message
                            .getContentRaw()
                            .replaceFirst(Pattern.quote(guildDB.getPrefix()), "")
                            .split("\\s+")
            )
    );

    String invoker = args.remove(0);
    Command command = client.getCommand(invoker);
    if (command == null) return;

    if (!Arrays.asList("dev", "config", "info").contains(command.getCategory().toLowerCase()) &&
            !guildDB.getEnabled().contains(command.getCategory())
    ) return;

    if (!userDB.getBadges().isPremium() && command.isPremium()) {
      sendEm(event.getChannel(),
              "Cool you found a premium feature! Become a premium user today and support our development!",
              Utils.Embeds.PRO
      ).queue();
    }

    if (
            event.getGuild().getSelfMember().hasPermission(command.getBotPerms()) &&
                    (event.getMember().hasPermission(command.getPerms()) ||
                            Objects.requireNonNull(message.getMember()).getRoles().stream().anyMatch(
                                    (x) -> guildDB.getIds().getAdminRoles().contains(x.getId())
                            )
                    )
    ) {
      if (command.getCd() > 0) {
        Optional<CD> oldCmd = memberDB
                .getCd()
                .stream()
                .filter((x) -> x.getName().equals(command.getName()))
                .findFirst();

        if (oldCmd.isPresent() && oldCmd.get().getCd() > Calendar.getInstance().getTimeInMillis()) {
          Utils.sendEm(
                  event.getChannel(),
                  client.getCross().getAsMention() + " You can use this command again after sometime!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        } else {
          CD newCmd = new CD();
          newCmd.setName(command.getName());
          newCmd.setCd(Calendar.getInstance().getTimeInMillis() + command.getCd());

          oldCmd.ifPresent(cd -> memberDB.getCd().remove(cd));
          memberDB.getCd().add(newCmd);
          client.getDbManager().save(memberDB);
        }
      }
      command.run(new CommandContext(client, event, args));
    } else {
      String uPerms = command.getPerms().length > 0 ?
              String.join(
                      " | ",
                      Arrays
                              .stream(command.getPerms()).map((p) -> "`" + p.getName() + "`")
                              .toArray(String[]::new)
              ) :
              "`None`";
      String botPerms = command.getBotPerms().length > 0 ?
              String.join(
                      " | ",
                      Arrays
                              .stream(command.getBotPerms()).map((p) -> "`" + p.getName() + "`")
                              .toArray(String[]::new)
              ) :
              "`None`";

      MessageEmbed embed = new EmbedBuilder()
              .setTitle("Not Enough Permissions")
              .setColor(Utils.getRed())
              .addField(
                      "User Perms",
                      "Users need the following permission(s) to run this command: " + uPerms,
                      false
              )
              .addField(
                      "Bot Perms",
                      "I need the following permission(s) to run this command: " + botPerms,
                      false
              )
              .setTimestamp(new Date().toInstant())
              .setFooter(
                      event.getJDA().getSelfUser().getName(),
                      event.getJDA().getSelfUser().getAvatarUrl()
              )
              .build();
      event.getChannel().sendMessage(embed).queue();
    }
  }
}
