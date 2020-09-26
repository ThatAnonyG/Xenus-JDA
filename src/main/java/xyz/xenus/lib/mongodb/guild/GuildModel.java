package xyz.xenus.lib.mongodb.guild;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;

public class GuildModel {
  private ObjectId id;

  private String gid;
  private String prefix = "=";
  private boolean msgDelete = false;
  private IDs ids = new IDs();
  private ArrayList<String> enabled = new ArrayList<>(Arrays.asList(
          "anime", "config", "dev", "economy", "fun", "info", "moderation", "music", "utility", "xp"
  ));
  private ArrayList<Logs> logs = new ArrayList<>();
  private Welcome welcome = new Welcome();
  private Economy economy = new Economy();
  private ArrayList<Tags> tags = new ArrayList<>();

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
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

  public ArrayList<String> getEnabled() {
    return enabled;
  }

  public void setEnabled(ArrayList<String> enabled) {
    this.enabled = enabled;
  }

  public ArrayList<Logs> getLogs() {
    return logs;
  }

  public void setLogs(ArrayList<Logs> logs) {
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
}
