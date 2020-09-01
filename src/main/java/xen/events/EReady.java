package xen.events;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xen.lib.client.XenClient;

public class EReady implements BaseEvent {
  private final XenClient client;
  private final String name;
  private final Logger LOG = LoggerFactory.getLogger(EReady.class);

  public EReady(XenClient client) {
    this.client = client;
    this.name = "ReadyEvent";
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
  public void handle(@NotNull GenericEvent event) {
    client.setStartTime(System.currentTimeMillis());

    Emote tick = event.getJDA().getEmoteById("694496723799638056");
    if (tick != null) client.setTick(tick.getAsMention());

    Emote cross = event.getJDA().getEmoteById("694496773451808798");
    if (cross != null) client.setCross(cross.getAsMention());

    LOG.info(
            event
                    .getJDA()
                    .getSelfUser()
                    .getName() + " is now online!"
    );
  }
}
