package xen.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import xen.lib.client.XenClient;

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
      client.getEvents().get(name).handle(event);
    }
  }
}
