package xyz.xenus.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.Actions;
import xyz.xenus.lib.mongodb.guild.Logs;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class Mute extends Command {
    public Mute() {
        super("mute");
        setCategory(Categories.MODERATION);
        setDescription("Mute a user so that they cannot chat.");
        setUsage("<Mention User | ID> [Reason]");
        setPerms(new Permission[]{Permission.MANAGE_ROLES});
        setBotPerms(new Permission[]{Permission.MANAGE_ROLES, Permission.MANAGE_CHANNEL});
    }

    @Override
    public void run(CommandContext ctx) {
        Optional<Member> memberOptional = Utils.getMember(ctx.getEvent().getMessage(), ctx.getArgs());
        if (memberOptional.isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " No user found with the given info!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        Member member = memberOptional.get();
        ctx.getArgs().remove(0);
        if (!Objects.requireNonNull(ctx.getEvent().getMember()).canInteract(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() +
                            " You cannot mute them because they are on a higher position than you!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (!ctx.getEvent().getGuild().getSelfMember().canInteract(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot mute them because they are mod/admin!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (ctx.getEvent().getGuild().getSelfMember().equals(member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " I cannot mute myself!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }
        if (Objects.equals(ctx.getEvent().getMember(), member)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " You cannot mute yourself!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        String reason = ctx.getArgs().isEmpty() ? "No reason provided!" : String.join(" ", ctx.getArgs());
        if (reason.length() > 300) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Reason cannot be more than 300 letters!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        Utils.getRole(ctx.getEvent().getMessage(), Collections.singletonList("muted")).ifPresentOrElse(
                (role) -> {
                    if (member.getRoles().contains(role)) {
                        Utils.sendEm(
                                ctx.getEvent().getChannel(),
                                ctx.getClient().getCross() + " This member is already muted!",
                                Utils.Embeds.ERROR
                        ).queue();
                        return;
                    }
                    ctx.getEvent().getGuild().addRoleToMember(member, role).queue();
                },
                () -> {
                    Role muteRole = ctx.getEvent().getGuild().createRole().setName("Muted").complete();
                    ctx.getEvent().getGuild().getChannels().stream().filter(
                            (c) -> ctx.getEvent().getGuild().getSelfMember().hasPermission(Permission.MANAGE_PERMISSIONS)
                    ).forEach(
                            (c) -> {
                                if (c.getType().equals(ChannelType.CATEGORY))
                                    c.upsertPermissionOverride(muteRole).deny(
                                            Permission.MESSAGE_WRITE,
                                            Permission.MESSAGE_ADD_REACTION,
                                            Permission.VOICE_CONNECT
                                    ).queue();
                                if (c.getType().equals(ChannelType.TEXT))
                                    c.upsertPermissionOverride(muteRole).deny(
                                            Permission.MESSAGE_WRITE,
                                            Permission.MESSAGE_ADD_REACTION
                                    ).queue();
                                if (c.getType().equals(ChannelType.VOICE))
                                    c.upsertPermissionOverride(muteRole).deny(
                                            Permission.VOICE_CONNECT
                                    ).queue();
                            }
                    );
                }
        );

        member.getUser().openPrivateChannel().queue(
                (c) -> Utils.sendEm(
                        c,
                        "You have been muted in " + ctx.getEvent().getGuild().getName() + "!\n**Reason:** " +
                                reason,
                        Utils.Embeds.BASE
                ).queue()
        );

        if (ctx.getGuildModel().getLogs().get(Logs.LogTypes.MUTE.name()).isEnabled()) {
            Actions action = new Actions();
            action.setMod(ctx.getEvent().getAuthor().getId());
            action.setUser(member.getId());
            action.setDate(System.currentTimeMillis());
            action.setReason(reason);

            ctx.getGuildModel().getLogs().get(Logs.LogTypes.MUTE.name()).getActions().add(action);
            ctx.getGuildModel().save();
        }

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " ***" + member.getUser().getAsTag() +
                        "*** has been muted in the server!" + "\n**Reason:** " + reason,
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendModLog(ctx.getEvent(), ctx.getGuildModel(), "User Muted", reason, member.getUser());
    }
}
