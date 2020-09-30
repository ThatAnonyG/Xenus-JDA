package xyz.xenus.commands.utils;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import java.util.Optional;

public class Say extends Command {
    public Say() {
        super("say");
        setCategory(Categories.UTILITY);
        setCd(5000);
        setDescription("Makes an embedded announcement in the server.");
        setUsage("[Mention Channel | ID] [Mention Role | ID | everyone] <Message>");
        setPerms(new Permission[]{Permission.MESSAGE_MENTION_EVERYONE});
        setBotPerms(new Permission[]{Permission.MESSAGE_MENTION_EVERYONE});
    }

    @Override
    public void run(CommandContext ctx) {
        Optional<TextChannel> optionalTextChannel = Utils.getChannel(ctx.getEvent().getMessage(), ctx.getArgs());
        if (optionalTextChannel.isPresent()) ctx.getArgs().remove(0);
        TextChannel channel = optionalTextChannel.orElse(ctx.getEvent().getChannel());

        Role role = ctx.hasSub(new String[]{"everyone"}, 0) ?
                ctx.getEvent().getGuild().getPublicRole() :
                Utils.getRole(ctx.getEvent().getMessage(), ctx.getArgs()).orElse(null);
        if (role != null) ctx.getArgs().remove(0);

        if (ctx.getArgs().isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Please provide some content for the announcement!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        MessageEmbed embed = Utils.embed()
                .setAuthor(
                        ctx.getEvent().getAuthor().getName(),
                        null,
                        ctx.getEvent().getAuthor().getEffectiveAvatarUrl()
                )
                .setDescription(String.join(" ", ctx.getArgs()))
                .build();

        MessageBuilder messageBuilder = new MessageBuilder().setEmbed(embed);
        if (role != null) messageBuilder.append(role.getAsMention());
        channel.sendMessage(messageBuilder.build()).queue();
    }
}
