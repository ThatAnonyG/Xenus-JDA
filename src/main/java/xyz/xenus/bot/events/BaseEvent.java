package xyz.xenus.bot.events;

import net.dv8tion.jda.api.events.GenericEvent;
import org.json.simple.parser.ParseException;
import xyz.xenus.lib.client.XenClient;

import java.io.IOException;

public interface BaseEvent {
    XenClient getClient();

    String getName();

    void handle(GenericEvent event) throws IOException, ParseException;
}
