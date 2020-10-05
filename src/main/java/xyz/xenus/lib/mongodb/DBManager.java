package xyz.xenus.lib.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.xenus.lib.mongodb.guild.GuildModel;
import xyz.xenus.lib.mongodb.guild.Logs;
import xyz.xenus.lib.mongodb.member.MemberModel;
import xyz.xenus.lib.mongodb.user.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DBManager {
    private final String connString;
    private final Logger LOG = LoggerFactory.getLogger(DBManager.class);
    private final HashMap<String, GuildModel> guildDbCache = new HashMap<>();
    private final HashMap<String, MemberModel> memberDbCache = new HashMap<>();
    private final HashMap<String, UserModel> userDbCache = new HashMap<>();
    private MongoDatabase db;

    public DBManager(String connString) {
        this.connString = connString;
    }

    public String getConnString() {
        return connString;
    }

    public MongoDatabase getDb() {
        return db;
    }

    public HashMap<String, GuildModel> getGuildDbCache() {
        return guildDbCache;
    }

    public HashMap<String, MemberModel> getMemberDbCache() {
        return memberDbCache;
    }

    public HashMap<String, UserModel> getUserDbCache() {
        return userDbCache;
    }

    public void login(String dbName) {
        CodecRegistry codecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        MongoClientSettings options = MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString(connString))
                .codecRegistry(codecRegistry)
                .retryWrites(true)
                .build();

        MongoClient conn = MongoClients.create(options);
        db = conn.getDatabase(dbName);
        LOG.info("Connected to database!");
    }

    public GuildModel init(@NotNull Guild guild) {
        GuildModel found = find(guild);
        if (found != null) return found;

        GuildModel guildModel = new GuildModel();
        guildModel.setModelId(guild.getId());
        guildModel.setManager(this);

        for (Logs.LogTypes type : Logs.LogTypes.values()) {
            Logs newLog = new Logs();
            newLog.setType(type);
            guildModel.getLogs().put(type.name(), newLog);
        }

        guildDbCache.put(guild.getId(), guildModel);
        return guildModel;
    }

    public MemberModel init(@NotNull Member member) {
        MemberModel found = find(member);
        if (found != null) return found;

        String key = member.getId() + "," + member.getGuild().getId();
        MemberModel memberModel = new MemberModel();
        memberModel.setModelId(key);
        memberModel.setManager(this);

        memberDbCache.put(key, memberModel);
        return memberModel;
    }

    public UserModel init(@NotNull User user) {
        UserModel found = find(user);
        if (found != null) return found;

        UserModel userModel = new UserModel();
        userModel.setModelId(user.getId());
        userModel.setManager(this);

        userDbCache.put(user.getId(), userModel);
        return userModel;
    }

    public GuildModel find(@NotNull Guild guild) {
        if (guildDbCache.containsKey(guild.getId())) return guildDbCache.get(guild.getId());

        GuildModel model = db.getCollection("guilds", GuildModel.class)
                .find(eq("model_id", guild.getId()))
                .projection(excludeId())
                .first();

        if (model != null) {
            guildDbCache.put(model.getModelId(), model);
            model.setManager(this);
        }

        return model;
    }

    public MemberModel find(@NotNull Member member) {
        String key = member.getId() + "," + member.getGuild().getId();
        if (memberDbCache.containsKey(key)) return memberDbCache.get(key);

        MemberModel model = db.getCollection("members", MemberModel.class)
                .find(eq("model_id", key))
                .projection(excludeId())
                .first();

        if (model != null) {
            memberDbCache.put(model.getModelId(), model);
            model.setManager(this);
        }

        return model;
    }

    public UserModel find(@NotNull User user) {
        if (userDbCache.containsKey(user.getId())) return userDbCache.get(user.getId());

        UserModel model = db.getCollection("users", UserModel.class)
                .find(eq("model_id", user.getId()))
                .projection(excludeId())
                .first();

        if (model != null) {
            userDbCache.put(model.getModelId(), model);
            model.setManager(this);
        }

        return model;
    }

    public List<Model> findMany(@NotNull Bson filter, Model.ModelType type) {
        List<Model> list = new ArrayList<>();

        switch (type) {
            case GUILD -> {
                MongoCollection<GuildModel> guilds = db.getCollection("guilds", GuildModel.class);
                guilds.find(filter).into(list);
                list.forEach((m) -> {
                    if (!guildDbCache.containsKey(m.getModelId())) {
                        guildDbCache.put(m.getModelId(), (GuildModel) m);
                        m.setManager(this);
                    }
                });
            }
            case MEMBER -> {
                MongoCollection<MemberModel> members = db.getCollection("members", MemberModel.class);
                members.find(filter).into(list);
                list.forEach((m) -> {
                    if (!memberDbCache.containsKey(m.getModelId())) {
                        memberDbCache.put(m.getModelId(), (MemberModel) m);
                        m.setManager(this);
                    }
                });
            }
            case USER -> {
                MongoCollection<UserModel> users = db.getCollection("users", UserModel.class);
                users.find(filter).into(list);
                list.forEach((m) -> {
                    if (!userDbCache.containsKey(m.getModelId())) {
                        userDbCache.put(m.getModelId(), (UserModel) m);
                        m.setManager(this);
                    }
                });
            }
        }

        return list;
    }
}
