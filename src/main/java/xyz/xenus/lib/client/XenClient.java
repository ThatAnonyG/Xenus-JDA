package xyz.xenus.lib.client;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.xenus.commands.config.*;
import xyz.xenus.commands.dev.Blacklist;
import xyz.xenus.commands.dev.Developer;
import xyz.xenus.commands.dev.Premium;
import xyz.xenus.commands.economy.*;
import xyz.xenus.commands.fun.Flip;
import xyz.xenus.commands.fun.Meme;
import xyz.xenus.commands.info.Configs;
import xyz.xenus.commands.info.Help;
import xyz.xenus.commands.info.Info;
import xyz.xenus.commands.info.Ping;
import xyz.xenus.commands.moderation.*;
import xyz.xenus.commands.utils.Avatar;
import xyz.xenus.commands.utils.Say;
import xyz.xenus.commands.utils.Tag;
import xyz.xenus.commands.utils.UserInfo;
import xyz.xenus.events.BaseEvent;
import xyz.xenus.events.EMessage;
import xyz.xenus.events.EReady;
import xyz.xenus.events.EventHandler;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.config.Config;
import xyz.xenus.lib.mongodb.DBManager;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.HashMap;

public class XenClient {
    private final String token;
    private final HashMap<String, BaseEvent> events = new HashMap<>();
    private final HashMap<String, Command> commands = new HashMap<>();
    private final Config config = new Config();
    private final DBManager dbManager = new DBManager(config.getConfig().getMongoDao().getUri());
    private final Logger LOG = LoggerFactory.getLogger(XenClient.class);
    private JDA api;
    private Economy ecoModule;
    private long startTime;
    private String tick;
    private String cross;
    private String premium;
    private String dev;

    public XenClient() {
        this.token = config.getConfig().getBotDao().getToken();
        registerEvents(new BaseEvent[]{
                new EReady(this),
                new EMessage(this)
        });
        registerCommands(new Command[]{
                // Config
                new ChannelXP(),
                new CoinAlert(),
                new DefaultRole(),
                new LeaveChannel(),
                new LevelUpAlert(),
                new LogChannel(),
                new MessageDelete(),
                new ModRole(),
                new Prefix(),
                new ReportChannel(),
                new TagConfig(),
                new Toggle(),
                new TopRole(),
                //new Verify(),
                new WelcomeChannel(),
                new WelcomeText(),
                new XPRate(),

                // Dev
                new Blacklist(),
                new Developer(),
                new Premium(),

                // Economy
                new Daily(),
                new Gamble(),
                new Jobs(),
                new Leaderboard(),
                new Pay(),
                new Profile(),
                //new Rank(),
                new Rep(),
                new Work(),

                // Fun
                new Flip(),
                new Meme(),

                // Info
                new Configs(),
                new Help(),
                new Info(),
                new Ping(),

                // Moderation
                new Ban(),
                new Cases(),
                new Kick(),
                new Mute(),
                new Report(),
                new Reports(),
                new Role(),
                new Unmute(),
                new Warn(),
                new Warns(),

                // Utils
                new Avatar(),
                new Say(),
                new Tag(),
                new UserInfo()
        });
    }

    public HashMap<String, BaseEvent> getEvents() {
        return events;
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public Config getConfig() {
        return config;
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    @Nullable
    public JDA getApi() {
        return api;
    }

    public Economy getEcoModule() {
        return ecoModule;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getTick() {
        return tick;
    }

    public void setTick(String tick) {
        this.tick = tick;
    }

    public String getCross() {
        return cross;
    }

    public void setCross(String cross) {
        this.cross = cross;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public void build() throws LoginException {
        dbManager.login(config.getConfig().getMongoDao().getDb());
        api = JDABuilder
                .createDefault(
                        token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_EMOJIS
                )
                .disableCache(
                        CacheFlag.ACTIVITY,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.VOICE_STATE
                )
                .setAutoReconnect(true)
                .addEventListeners(new EventHandler(this))
                .setActivity(Activity.watching("https://xenus.xyz/"))
                .build();
        ecoModule = new Economy(this);
    }

    private void registerEvents(@NotNull BaseEvent[] evtArr) {
        for (BaseEvent event : evtArr) {
            if (commands.containsKey(event.getName())) {
                LOG.info(event.getName() + ": Event already registered!");
            }
            events.put(event.getName(), event);
            LOG.info(event.getName() + ": Event registered!");
        }
    }

    private void registerCommands(@NotNull Command[] cmdArr) {
        for (Command command : cmdArr) {
            if (commands.containsKey(command.getName())) {
                LOG.info(command.getName() + ": Command already registered!");
            }
            commands.put(command.getName().toLowerCase(), command);
            LOG.info(command.getName() + ": Command registered!");
        }
    }

    @Nullable
    public Command getCommand(String name) {
        Object[] found = commands
                .values()
                .stream()
                .filter((c) -> c.getName().toLowerCase().equals(name.toLowerCase())
                        || Arrays.stream(c.getAliases()).anyMatch((a) -> a.toLowerCase().equals(name.toLowerCase()))
                ).toArray();

        if (found.length == 0) return null;
        return (Command) found[0];
    }
}
