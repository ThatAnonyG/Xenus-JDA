package xyz.xenus.bot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.client.XenClient;
import xyz.xenus.lib.mongodb.guild.GuildModel;
import xyz.xenus.lib.mongodb.member.MemberModel;

import java.util.Optional;

public class EMemberJoin implements BaseEvent {
    private final XenClient client;
    private final String name;

    public EMemberJoin(XenClient client) {
        this.client = client;
        this.name = "GuildMemberJoinEvent";
    }

    @Override
    public XenClient getClient() {
        return client;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void handle(@NotNull GenericEvent rawEvent) {
        GuildMemberJoinEvent event = (GuildMemberJoinEvent) rawEvent;
        if (event.getUser().isBot()) return;

        GuildModel guildModel = client.getDbManager().find(event.getGuild());
        if (guildModel == null) return;

        Role defRole = event.getGuild().getRoleById(guildModel.getWelcome().getRole());
        TextChannel channel = event.getGuild().getTextChannelById(guildModel.getWelcome().getJoins());
        Optional<Role> muted = event.getGuild().getRoles().stream().filter(
                (r) -> r.getName().toLowerCase().equals("muted")
        ).findFirst();

        if (channel != null && channel.canTalk()) {
            EmbedBuilder embed = Utils.embed()
                    .setTitle(event.getUser().getAsTag() + "joined the server!")
                    .setAuthor("New Member Joined!", event.getGuild().getIconUrl())
                    .setColor(Utils.getHex())
                    .setDescription(guildModel.getWelcome().getMessage())
                    .setImage(event.getUser().getEffectiveAvatarUrl())
                    .setFooter(
                            "You are the " + event.getGuild().getMemberCount() + "th member",
                            "https://imgur.com/orfFkI6.png"
                    );
            channel.sendMessage(embed.build()).queue();
        }

        if (event.getGuild().getSelfMember().canInteract(event.getMember())) {
            if (defRole != null)
                event.getGuild().addRoleToMember(event.getMember(), defRole).queue();

            if (muted.isPresent()) {
                MemberModel memberModel = client.getDbManager().find(event.getMember());
                if (memberModel != null && memberModel.isMuted())
                    event.getGuild().addRoleToMember(event.getMember(), muted.get()).queue();
            }
        }
    }
}
