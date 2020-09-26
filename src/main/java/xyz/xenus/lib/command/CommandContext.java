package xyz.xenus.lib.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.xenus.lib.client.XenClient;

import java.util.ArrayList;
import java.util.Arrays;

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

  public boolean hasSub(String[] options, int pos) {
    if (args.isEmpty()) return false;
    if (pos > (args.size() - 1)) return false;
    return Arrays.asList(options).contains(args.get(pos).toLowerCase());
  }
}
