package xyz.xenus.lib.mongodb.guild;

import com.mongodb.client.MongoCollection;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.mongodb.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class GuildModel extends Model {
    private String prefix = "=";
    private boolean msgDelete = false;
    private IDs ids = new IDs();
    private ArrayList<Command.Categories> enabled = new ArrayList<>(Arrays.asList(Command.Categories.values()));
    private HashMap<String, Logs> logs = new HashMap<>();
    private Welcome welcome = new Welcome();
    private Economy economy = new Economy();
    private ArrayList<Tags> tags = new ArrayList<>();

    public GuildModel() {
        super(ModelType.GUILD);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isMsgDelete() {
        return msgDelete;
    }

    public void setMsgDelete(boolean msgDelete) {
        this.msgDelete = msgDelete;
    }

    public IDs getIds() {
        return ids;
    }

    public void setIds(IDs ids) {
        this.ids = ids;
    }

    public ArrayList<Command.Categories> getEnabled() {
        return enabled;
    }

    public void setEnabled(ArrayList<Command.Categories> enabled) {
        this.enabled = enabled;
    }

    public HashMap<String, Logs> getLogs() {
        return logs;
    }

    public void setLogs(HashMap<String, Logs> logs) {
        this.logs = logs;
    }

    public Welcome getWelcome() {
        return welcome;
    }

    public void setWelcome(Welcome welcome) {
        this.welcome = welcome;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

    public ArrayList<Tags> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tags> tags) {
        this.tags = tags;
    }

    @Override
    public GuildModel save() {
        MongoCollection<GuildModel> col = getManager().getDb().getCollection("guilds", GuildModel.class);
        GuildModel model = col.findOneAndReplace(eq("model_id", getModelId()), this);
        if (model == null) col.insertOne(this);
        getManager().getGuildDbCache().put(this.getModelId(), this);
        return this;
    }
}
