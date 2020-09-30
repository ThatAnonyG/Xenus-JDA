package xyz.xenus.commands.utils;

import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.Tags;

import java.util.Optional;

public class Tag extends Command {
    public Tag() {
        super("tag");
        setCategory(Categories.UTILITY);
        setCd(5000);
        setDescription("Show a list of custom tags or a particular tag's info.");
        setUsage("[Tag Name] - Leave blank to see available tags list");
    }

    @Override
    public void run(CommandContext ctx) {
        if (ctx.getArgs().isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    "Available tags in the server:\n" + String.join(
                            " | ",
                            ctx.getGuildModel().getTags().stream().map(
                                    (x) -> "`" + x.getName() + "`"
                            ).toArray(String[]::new)
                    ),
                    Utils.Embeds.BASE
            ).queue();
            return;
        }

        Optional<Tags> tag = ctx.getGuildModel().getTags().stream().filter(
                (x) -> x.getName().toLowerCase().equals(ctx.getArgs().get(0).toLowerCase())
        ).findFirst();
        if (tag.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No tag found with the given name!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        ctx.getEvent().getChannel().sendMessage(
                Utils.embed()
                        .setTitle(tag.get().getName())
                        .setDescription(tag.get().getInfo())
                        .setFooter(
                                "Requested by " + ctx.getEvent().getAuthor().getAsTag(),
                                ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl()
                        )
                        .build()
        ).queue();
    }
}
