package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;
import xyz.xenus.lib.mongodb.guild.Logs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Toggle extends Command {
  public Toggle() {
    super("toggle");
    setCategory(Categories.CONFIG);
    setDescription("Toggle bot categories and moderation logs.");
    setUsage("<cat | logs> <Feature Name>");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    GuildModel guildModel = (GuildModel) ctx.getClient().getDbManager().find(
            ctx.getEvent().getGuild()
    );

    if (!ctx.hasSub(new String[]{"cat", "logs"}, 0)) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() +
                      " Please specify the type - `cat` for Category `log` for Moderation Logs!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    switch (ctx.getArgs().get(0).toLowerCase()) {
      case "cat" -> {
        ArrayList<String> cats = new ArrayList<>(
                Arrays.asList(
                        Arrays.stream(Categories.values()).map(
                                (x) -> x.toString().toLowerCase()
                        ).toArray(String[]::new)
                )
        );
        if (!cats.contains(ctx.getArgs().get(1).toLowerCase())) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() +
                          " Please specify a proper category:\n\n" +
                          String.join(
                                  " | ", cats.stream().map(
                                          (x) -> "`" + x + "`"
                                  ).toArray(String[]::new)
                          ),
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }

        String msg;
        if (guildModel.getEnabled().contains(ctx.getArgs().get(0).toLowerCase())) {
          guildModel.getEnabled().remove(ctx.getArgs().get(0).toLowerCase());
          msg = "Disabled the `" + ctx.getArgs().get(0).toLowerCase() + "` category!";
        } else {
          guildModel.getEnabled().add(ctx.getArgs().get(0).toLowerCase());
          msg = "Enabled the `" + ctx.getArgs().get(0).toLowerCase() + "` category!";
        }

        ctx.getClient().getDbManager().save(guildModel);
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " " + msg,
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(ctx.getEvent(), guildModel, "Toggled Command Category", msg);
      }

      case "logs" -> {
        ArrayList<String> logs = new ArrayList<>(
                Arrays.asList("warn", "mute", "unmute", "kick", "ban")
        );

        if (!logs.contains(ctx.getArgs().get(1).toLowerCase())) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() +
                          " Please specify a proper log type:\n\n" +
                          String.join(
                                  " | ", logs.stream().map(
                                          (x) -> "`" + x + "`"
                                  ).toArray(String[]::new)
                          ),
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }

        String msg;
        Optional<Logs> logFound = guildModel.getLogs().stream().filter(
                (x) -> x.getType().toLowerCase().equals(ctx.getArgs().get(0).toLowerCase())
        ).findFirst();

        if (logFound.isPresent() && logFound.get().isEnabled()) {
          guildModel.getLogs().stream().filter(
                  (x) -> x.getType().toLowerCase().equals(ctx.getArgs().get(0).toLowerCase())
          ).findFirst().ifPresent((x) -> x.setEnabled(false));
          msg = "Disabled the `" + ctx.getArgs().get(0).toLowerCase() + "` moderation log!";
        } else {
          Logs newLog = new Logs();
          newLog.setType(ctx.getArgs().get(0).toLowerCase());
          newLog.setEnabled(true);
          guildModel.getLogs().add(newLog);
          msg = "Enabled the `" + ctx.getArgs().get(0).toLowerCase() + "` moderation log!";
        }

        ctx.getClient().getDbManager().save(guildModel);
        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " " + msg,
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(ctx.getEvent(), guildModel, "Toggled Moderation Logs", msg);
      }
    }
  }
}
