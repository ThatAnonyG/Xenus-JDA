package xen.lib.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xen.lib.mongodb.guild.GuildModel;
import xen.lib.mongodb.guild.Logs;
import xen.lib.mongodb.member.MemberModel;
import xen.lib.mongodb.user.UserModel;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DBManager {
  private final String connString;
  private final Logger LOG = LoggerFactory.getLogger(DBManager.class);
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

  public void init(@NotNull Object obj) {
    String classType = obj.getClass().getSimpleName();
    if (find(obj) != null) return;

    switch (classType) {
      case "GuildImpl":
        Guild guild = (Guild) obj;
        MongoCollection<GuildModel> guilds = db.getCollection("guilds", GuildModel.class);

        GuildModel guildModel = new GuildModel();
        guildModel.setGid(guild.getId());

        String[] logs = new String[]{"warn", "mute", "unmute", "kick", "ban"};
        for (String log : logs) {
          Logs newLog = new Logs();
          newLog.setType(log);
          guildModel.getLogs().add(newLog);
        }

        guilds.insertOne(guildModel);
        break;

      case "MemberImpl":
        Member member = (Member) obj;
        MongoCollection<MemberModel> members = db.getCollection("members", MemberModel.class);

        MemberModel memberModel = new MemberModel();
        memberModel.setMid(member.getId());
        memberModel.setGid(member.getGuild().getId());
        members.insertOne(memberModel);
        break;

      case "UserImpl":
        User user = (User) obj;
        MongoCollection<UserModel> users = db.getCollection("users", UserModel.class);

        UserModel userModel = new UserModel();
        userModel.setUid(user.getId());
        users.insertOne(userModel);
        break;
    }
  }

  public Object find(@NotNull Object obj) {
    String classType = obj.getClass().getSimpleName();

    switch (classType) {
      case "GuildImpl":
        Guild guild = (Guild) obj;
        MongoCollection<GuildModel> guilds = db.getCollection("guilds", GuildModel.class);

        return guilds
                .find(eq("gid", guild.getId()))
                .projection(excludeId())
                .first();

      case "MemberImpl":
        Member member = (Member) obj;
        MongoCollection<MemberModel> members = db.getCollection("members", MemberModel.class);

        return members
                .find(
                        and(
                                eq("gid", member.getGuild().getId()),
                                eq("mid", member.getId())
                        )
                )
                .projection(excludeId())
                .first();

      case "UserImpl":
        User user = (User) obj;
        MongoCollection<UserModel> users = db.getCollection("users", UserModel.class);

        return users
                .find(eq("uid", user.getId()))
                .projection(excludeId())
                .first();
    }

    return null;
  }

  public Object save(@NotNull Object obj) {
    FindOneAndReplaceOptions options = new FindOneAndReplaceOptions()
            .returnDocument(ReturnDocument.AFTER);
    String classType = obj.getClass().getSimpleName();

    switch (classType) {
      case "GuildModel":
        GuildModel guild = (GuildModel) obj;
        MongoCollection<GuildModel> guilds = db.getCollection("guilds", GuildModel.class);
        return guilds.findOneAndReplace(eq("gid", guild.getGid()), guild, options);

      case "MemberModel":
        MemberModel member = (MemberModel) obj;
        MongoCollection<MemberModel> members = db.getCollection("members", MemberModel.class);
        return members.findOneAndReplace(
                and(eq("mid", member.getMid()), eq("gid", member.getGid())),
                member,
                options
        );

      case "UserModel":
        UserModel user = (UserModel) obj;
        MongoCollection<UserModel> users = db.getCollection("users", UserModel.class);
        return users.findOneAndReplace(eq("uid", user.getUid()), user, options);
    }

    return null;
  }
}
