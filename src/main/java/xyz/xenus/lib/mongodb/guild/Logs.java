package xyz.xenus.lib.mongodb.guild;

import java.util.ArrayList;

public class Logs {
    private LogTypes type;
    private boolean enabled = false;
    private ArrayList<Actions> actions = new ArrayList<>();

    public LogTypes getType() {
        return type;
    }

    public void setType(LogTypes type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ArrayList<Actions> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Actions> actions) {
        this.actions = actions;
    }

    public enum LogTypes {
        WARN,
        MUTE,
        UNMUTE,
        KICK,
        BAN
    }
}
