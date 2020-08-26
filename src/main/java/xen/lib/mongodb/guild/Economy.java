package xen.lib.mongodb.guild;

import java.util.ArrayList;

public class Economy {
  private float xpRate = 1;
  private ArrayList<String> blocked = new ArrayList<>();
  private String xpLead = "";
  private boolean coinAlert = true;

  public float getXpRate() {
    return xpRate;
  }

  public void setXpRate(float xpRate) {
    this.xpRate = xpRate;
  }

  public ArrayList<String> getBlocked() {
    return blocked;
  }

  public void setBlocked(ArrayList<String> blocked) {
    this.blocked = blocked;
  }

  public String getXpLead() {
    return xpLead;
  }

  public void setXpLead(String xpLead) {
    this.xpLead = xpLead;
  }

  public boolean isCoinAlert() {
    return coinAlert;
  }

  public void setCoinAlert(boolean coinAlert) {
    this.coinAlert = coinAlert;
  }
}
