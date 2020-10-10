package xyz.xenus.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.client.XenClient;
import xyz.xenus.lib.mongodb.guild.GuildModel;

public class EMemberLeave implements BaseEvent {
    private final XenClient client;
    private final String name;

    public EMemberLeave(XenClient client) {
        this.client = client;
        this.name = "GuildMemberRemoveEvent";
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
        GuildMemberRemoveEvent event = (GuildMemberRemoveEvent) rawEvent;
        if (event.getUser().isBot()) return;

        GuildModel guildModel = client.getDbManager().find(event.getGuild());
        if (guildModel == null) return;

        TextChannel channel = event.getGuild().getTextChannelById(guildModel.getWelcome().getLeaves());

        if (channel != null && channel.canTalk()) {
            EmbedBuilder embed = Utils.embed()
                    .setTitle(event.getUser().getAsTag() + "left the server!")
                    .setAuthor("Member Left!", event.getGuild().getIconUrl())
                    .setColor(Utils.getHex())
                    .setDescription("Sorry to see you go. Hope you enjoyed your stay!")
                    .setImage(event.getUser().getEffectiveAvatarUrl())
                    .setFooter(
                            "Server is at " + event.getGuild().getMemberCount() + "members",
                            "https://imgur.com/orfFkI6.png"
                    );
            channel.sendMessage(embed.build()).queue();
        }
    }
}
