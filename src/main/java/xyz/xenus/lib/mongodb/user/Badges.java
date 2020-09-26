package xyz.xenus.lib.mongodb.user;

import java.util.ArrayList;

public class Badges {
  private boolean blacklisted = false;
  private boolean premium = false;
  private boolean developer = false;
  private ArrayList<String> other = new ArrayList<>();

  public boolean isBlacklisted() {
    return blacklisted;
  }

  public void setBlacklisted(boolean blacklisted) {
    this.blacklisted = blacklisted;
  }

  public boolean isPremium() {
    return premium;
  }

  public void setPremium(boolean premium) {
    this.premium = premium;
  }

  public boolean isDeveloper() {
    return developer;
  }

  public void setDeveloper(boolean developer) {
    this.developer = developer;
  }

  public ArrayList<String> getOther() {
    return other;
  }

  public void setOther(ArrayList<String> other) {
    this.other = other;
  }
}
