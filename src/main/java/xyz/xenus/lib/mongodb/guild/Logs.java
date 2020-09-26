package xyz.xenus.lib.mongodb.guild;

import java.util.ArrayList;

public class Logs {
  private String type;
  private boolean enabled = false;
  private ArrayList<ActionDao> actions = new ArrayList<>();

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public ArrayList<ActionDao> getActions() {
    return actions;
  }

  public void setActions(ArrayList<ActionDao> actions) {
    this.actions = actions;
  }
}
