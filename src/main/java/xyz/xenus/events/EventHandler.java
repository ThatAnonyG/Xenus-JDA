package xyz.xenus.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import xyz.xenus.lib.client.XenClient;

import java.io.IOException;

public class EventHandler implements EventListener {
  public final XenClient client;

  public EventHandler(XenClient client) {
    this.client = client;
  }

  public XenClient getClient() {
    return client;
  }

  @Override
  public void onEvent(@NotNull GenericEvent event) {
    String name = event.getClass().getSimpleName();
    if (client.getEvents().containsKey(name)) {
      try {
        client.getEvents().get(name).handle(event);
      } catch (IOException | ParseException e) {
        e.printStackTrace();
      }
    }
  }
}
