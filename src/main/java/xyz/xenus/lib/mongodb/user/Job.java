package xyz.xenus.lib.mongodb.user;

public class Job {
  private String name = "None";
  private int ready = 0;
  private int salary = 0;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getReady() {
    return ready;
  }

  public void setReady(int ready) {
    this.ready = ready;
  }

  public int getSalary() {
    return salary;
  }

  public void setSalary(int salary) {
    this.salary = salary;
  }
}
