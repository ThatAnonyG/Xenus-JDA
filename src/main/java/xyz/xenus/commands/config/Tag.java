package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.GuildModel;
import xyz.xenus.lib.mongodb.guild.Tags;

import java.util.Arrays;

public class Tag extends Command {
  public Tag() {
    super("tag");
    setCategory(Categories.CONFIG);
    setDescription("Lets you to add/remove custom information tags.");
    setUsage("[add | del] <Name> [Description]");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(CommandContext ctx) {
    GuildModel guildModel = (GuildModel) ctx.getClient().getDbManager().find(
            ctx.getEvent().getGuild()
    );

    if (
            ctx.getArgs().isEmpty() ||
                    !Arrays.asList("add", "del").contains(ctx.getArgs().get(0).toLowerCase())
    )
      ctx.getArgs().add(0, "add");

    switch (ctx.getArgs().get(0).toLowerCase()) {
      case "add" -> {
        if (ctx.getArgs().size() < 2) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() + " No tag name/description provided!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }
        String name = ctx.getArgs().get(1);
        String desc = String.join(
                " ",
                Arrays.copyOfRange(
                        ctx.getArgs().toArray(String[]::new), 2, ctx.getArgs().size()
                )
        );

        Tags tag = new Tags();
        tag.setName(name);
        tag.setInfo(desc);
        guildModel.getTags().add(tag);
        ctx.getClient().getDbManager().save(guildModel);

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " The `" + name + "` tag has been added!",
                Utils.Embeds.SUCCESS
        ).queue();
      }

      case "del" -> {
        if (ctx.getArgs().size() < 1) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() + " No tag name provided!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }
        String name = ctx.getArgs().get(1);

        guildModel.getTags().remove(
                guildModel.getTags().stream().filter(
                        (x) -> x.getName().equals(name)
                ).toArray(Tags[]::new)[0]
        );
        ctx.getClient().getDbManager().save(guildModel);

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " The `" + name + "` tag has been removed!",
                Utils.Embeds.SUCCESS
        ).queue();
      }
    }
  }
}
