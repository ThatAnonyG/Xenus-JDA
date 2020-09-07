package xen.commands.config;

import net.dv8tion.jda.api.Permission;
import xen.lib.command.Command;
import xen.lib.command.CommandContext;
import xen.lib.mongodb.guild.GuildModel;
import xen.lib.mongodb.guild.Tags;
import xen.lib.utils.Utils;

import java.util.Arrays;

public class Tag extends Command {
  private GuildModel guildModel;

  public Tag() {
    super("tag");
    setCategory(Categories.CONFIG);
    setDescription("Lets you to add/remove custom information tags.");
    setUsage("[add | del] <Name> [Description]");
    setPerms(new Permission[]{Permission.ADMINISTRATOR});
  }

  @Override
  public void run(CommandContext ctx) {
    guildModel = (GuildModel) ctx
            .getClient()
            .getDbManager()
            .find(ctx.getEvent().getGuild());

    if (!Arrays.asList("add", "del").contains(ctx.getArgs().get(0).toLowerCase()))
      ctx.getArgs().add(0, "add");

    switch (ctx.getArgs().get(0).toLowerCase()) {
      case "add" -> {
        String name = ctx.getArgs().get(1);
        String desc = String.join(
                " ",
                Arrays.copyOfRange(
                        ctx.getArgs().toArray(String[]::new), 2, ctx.getArgs().size()
                )
        );
        if (name == null || desc.length() == 0) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() + " No tag name/description provided!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }

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
        String name = ctx.getArgs().get(1);
        if (name == null) {
          Utils.sendEm(
                  ctx.getEvent().getChannel(),
                  ctx.getClient().getCross() + " No tag name provided!",
                  Utils.Embeds.ERROR
          ).queue();
          return;
        }

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
