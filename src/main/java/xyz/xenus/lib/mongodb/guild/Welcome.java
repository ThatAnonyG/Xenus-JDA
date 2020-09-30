package xyz.xenus.lib.mongodb.guild;

public class Welcome {
    private String joins = "";
    private String leaves = "";
    private String role = "";
    private String message = "";
    private String verifyRole = "";
    private byte verifyChances = 0;
    private long verifyTimeout = 0;

    public String getJoins() {
        return joins;
    }

    public void setJoins(String joins) {
        this.joins = joins;
    }

    public String getLeaves() {
        return leaves;
    }

    public void setLeaves(String leaves) {
        this.leaves = leaves;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVerifyRole() {
        return verifyRole;
    }

    public void setVerifyRole(String verifyRole) {
        this.verifyRole = verifyRole;
    }

    public byte getVerifyChances() {
        return verifyChances;
    }

    public void setVerifyChances(byte chances) {
        this.verifyChances = chances;
    }

    public long getVerifyTimeout() {
        return verifyTimeout;
    }

    public void setVerifyTimeout(long verifyTimeout) {
        this.verifyTimeout = verifyTimeout;
    }
}
