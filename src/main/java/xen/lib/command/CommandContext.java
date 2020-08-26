package xen.lib.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xen.lib.client.XenClient;

import java.util.ArrayList;

public class CommandContext {
  private final XenClient client;
  private final GuildMessageReceivedEvent event;
  private final ArrayList<String> args;

  public CommandContext(XenClient client, GuildMessageReceivedEvent event, ArrayList<String> args) {
    this.client = client;
    this.event = event;
    this.args = args;
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
}
