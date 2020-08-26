package xen.lib.mongodb.member;

public class Actions {
  private String mod = "";
  private String reason = "";
  private int date = 0;

  public String getMod() {
    return mod;
  }

  public void setMod(String mod) {
    this.mod = mod;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public int getDate() {
    return date;
  }

  public void setDate(int date) {
    this.date = date;
  }
}
