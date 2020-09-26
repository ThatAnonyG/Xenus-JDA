package xyz.xenus.lib.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class Config {
  private final String type;
  private ConfigDao config;

  public Config() {
    Dotenv env = Dotenv.load();
    this.type = env.get("TYPE");
  }

  public String getType() {
    return type;
  }

  public ConfigDao getConfig() {
    return config;
  }

  public Config load() {
    Yaml yaml = new Yaml();
    InputStream data = this
            .getClass()
            .getClassLoader()
            .getResourceAsStream(type + ".yml");
    config = yaml.load(data);
    return this;
  }
}
