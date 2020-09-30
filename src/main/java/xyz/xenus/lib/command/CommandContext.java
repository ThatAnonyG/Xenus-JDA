package xyz.xenus.lib.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.xenus.lib.client.XenClient;
import xyz.xenus.lib.mongodb.guild.GuildModel;
import xyz.xenus.lib.mongodb.member.MemberModel;
import xyz.xenus.lib.mongodb.user.UserModel;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandContext {
    private final XenClient client;
    private final GuildMessageReceivedEvent event;
    private final ArrayList<String> args;
    private GuildModel guildModel;
    private UserModel userModel;
    private MemberModel memberModel;

    public CommandContext(
            XenClient client,
            GuildMessageReceivedEvent event,
            ArrayList<String> args,
            GuildModel guildModel,
            UserModel userModel,
            MemberModel memberModel) {
        this.client = client;
        this.event = event;
        this.args = args;
        this.guildModel = guildModel;
        this.userModel = userModel;
        this.memberModel = memberModel;
    }

    public XenClient getClient() {
        return client;
    }

    public GuildMessageReceivedEvent getEvent() {
        return event;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public GuildModel getGuildModel() {
        return guildModel;
    }

    public void setGuildModel(GuildModel guildModel) {
        this.guildModel = guildModel;
    }

    public MemberModel getMemberModel() {
        return memberModel;
    }

    public void setMemberModel(MemberModel memberModel) {
        this.memberModel = memberModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public boolean hasSub(String[] options, int pos) {
        if (args.isEmpty()) return false;
        if (pos > (args.size() - 1)) return false;
        return Arrays.asList(options).contains(args.get(pos).toLowerCase());
    }
}
