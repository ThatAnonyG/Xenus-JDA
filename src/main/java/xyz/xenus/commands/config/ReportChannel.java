package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;

import java.util.concurrent.atomic.AtomicReference;

public class ReportChannel extends Command {
  public ReportChannel() {
    super("reportChannel");
    setCategory(Categories.CONFIG);
    setDescription("Let's you set a channel for report logs.");
    setUsage("[Channel Mention | ID] - Leave it blank to disable report logs");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(@NotNull CommandContext ctx) {
    GuildModel guildModel = (GuildModel) ctx.getClient().getDbManager().find(
            ctx.getEvent().getGuild()
    );

    if (ctx.getArgs().isEmpty()) {
      guildModel.getIds().setReports("");
      ctx.getClient().getDbManager().save(guildModel);
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getTick() + " Report logs have been disabled!",
              Utils.Embeds.SUCCESS
      ).queue();
      return;
    }

    AtomicReference<TextChannel> channel = new AtomicReference<>();
    if (
            !ctx.getEvent().getMessage().getMentionedChannels().isEmpty() &&
                    ctx.getEvent().getGuild().getTextChannels().contains(
                            ctx.getEvent().getMessage().getMentionedChannels().get(0)
                    )
    )
      channel.set(ctx.getEvent().getMessage().getMentionedChannels().get(0));
    if (Utils.isSnowflake(ctx.getArgs().get(0)))
      channel.set(ctx.getEvent().getGuild().getTextChannelById(ctx.getArgs().get(0)));
    if (channel.get() == null) {
      Utils.sendEm(
              ctx.getEvent().getChannel(),
              ctx.getClient().getCross() + " No channel found with the given info!",
              Utils.Embeds.ERROR
      ).queue();
      return;
    }

    guildModel.getIds().setReports(channel.get().getId());
    ctx.getClient().getDbManager().save(guildModel);

    Utils.sendEm(
            ctx.getEvent().getChannel(),
            ctx.getClient().getTick() + " Report logs channel changed to " +
                    channel.get().getAsMention() + "!",
            Utils.Embeds.SUCCESS
    ).queue();
    Utils.sendConfigLog(
            ctx.getEvent(),
            guildModel,
            "Changed Report Log Channel",
            "Report logs channel changed to " + channel.get().getAsMention() +
                    " | " + channel.get().getId()
    );
  }
}
