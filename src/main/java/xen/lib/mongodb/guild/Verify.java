package xen.lib.mongodb.guild;

public class Verify {
  private boolean enable = false;
  private String role = "";
  private int time = 0;
  private int chance = 0;

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public int getChance() {
    return chance;
  }

  public void setChance(int chance) {
    this.chance = chance;
  }
}
