package xen.lib.mongodb.member;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class MemberModel {
  private ObjectId id;

  private String mid;
  private String gid;
  private int mute = 0;
  private ArrayList<Actions> warns = new ArrayList<>();
  private ArrayList<Actions> reports = new ArrayList<>();
  private ArrayList<CD> cd = new ArrayList<>();
  private Economy economy = new Economy();

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getMid() {
    return mid;
  }

  public void setMid(String mid) {
    this.mid = mid;
  }

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
  }

  public int getMute() {
    return mute;
  }

  public void setMute(int mute) {
    this.mute = mute;
  }

  public ArrayList<Actions> getWarns() {
    return warns;
  }

  public void setWarns(ArrayList<Actions> warns) {
    this.warns = warns;
  }

  public ArrayList<Actions> getReports() {
    return reports;
  }

  public void setReports(ArrayList<Actions> reports) {
    this.reports = reports;
  }

  public ArrayList<CD> getCd() {
    return cd;
  }

  public void setCd(ArrayList<CD> cd) {
    this.cd = cd;
  }

  public Economy getEconomy() {
    return economy;
  }

  public void setEconomy(Economy economy) {
    this.economy = economy;
  }
}
