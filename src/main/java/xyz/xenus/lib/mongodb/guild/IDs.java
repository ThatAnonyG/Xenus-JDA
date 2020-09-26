package xyz.xenus.lib.mongodb.guild;

import java.util.ArrayList;

public class IDs {
  private ArrayList<String> adminRoles = new ArrayList<>();
  private String logs = "";
  private String reports = "";

  public ArrayList<String> getAdminRoles() {
    return adminRoles;
  }

  public void setAdminRoles(ArrayList<String> adminRoles) {
    this.adminRoles = adminRoles;
  }

  public String getLogs() {
    return logs;
  }

  public void setLogs(String logs) {
    this.logs = logs;
  }

  public String getReports() {
    return reports;
  }

  public void setReports(String reports) {
    this.reports = reports;
  }
}
