package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import xen.lib.Utils;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;

import java.util.Arrays;

public class ChannelXP extends Command {
  private GuildModel guildModel;

  public ChannelXP() {
    super("channelXP");
    setCategory(Categories.CONFIG);
    setDescription("Let's you disable/enable XP gain in the current channel.");
    setUsage("<Y | N>");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    guildModel = (GuildModel) ctx.getClient().getDbManager().find(ctx.getEvent().getGuild());
    TextChannel channel = ctx.getEvent().getChannel();

    if (
            ctx.getArgs().isEmpty() ||
                    Arrays.asList("y", "n").contains(ctx.getArgs().get(0).toLowerCase())
    ) {
      Utils.sendEm(
              channel,
              ctx.getClient().getCross() +
                      " Use a proper option - `Y` for enable and `N` for disable!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    switch (ctx.getArgs().get(0).toLowerCase()) {
      case "y" -> {
        if (!guildModel.getEconomy().getBlocked().contains(channel.getId())) {
          Utils.sendEm(
                  channel,
                  ctx.getClient().getCross() +
                          " XP gain is already enabled in this channel!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }

        guildModel.getEconomy().getBlocked().remove(channel.getId());
        ctx.getClient().getDbManager().save(guildModel);

        Utils.sendEm(
                channel,
                ctx.getClient().getTick() +
                        " Enabled XP gain in this channel!",
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(
                ctx.getEvent(),
                guildModel,
                "Removed No XP Channel",
                "XP gain enabled in " + channel.getAsMention() + " | " + channel.getId()
        );
      }

      case "n" -> {
        if (guildModel.getEconomy().getBlocked().contains(channel.getId())) {
          Utils.sendEm(
                  channel,
                  ctx.getClient().getCross() +
                          " XP gain is already disabled in this channel!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }

        guildModel.getEconomy().getBlocked().add(channel.getId());
        ctx.getClient().getDbManager().save(guildModel);

        Utils.sendEm(
                channel,
                ctx.getClient().getTick() +
                        " Disabled XP gain in this channel!",
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(
                ctx.getEvent(),
                guildModel,
                "Added No XP Channel",
                "XP gain disabled in " + channel.getAsMention() + " | " + channel.getId()
        );
      }
    }
  }
}
