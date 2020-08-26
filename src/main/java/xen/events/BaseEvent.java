package xen.events;

import net.dv8tion.jda.api.events.GenericEvent;
import xen.lib.client.XenClient;

public interface BaseEvent {
  XenClient getClient();

  String getName();

  void handle(GenericEvent event);
}
