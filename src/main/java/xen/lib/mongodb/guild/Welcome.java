package xen.lib.mongodb.guild;

public class Welcome {
  private String joins = "";
  private String leaves = "";
  private String role = "";
  private String message = "";

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
}
