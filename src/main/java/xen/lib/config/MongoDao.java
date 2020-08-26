package xen.lib.config;

public class MongoDao {
  private String uri;
  private String db;

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }
}
