package xyz.xenus.lib.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class Config {
    private final String type;
    private final ConfigDao config;

    public Config() {
        Dotenv env = Dotenv.load();
        this.type = env.get("TYPE");
        Yaml yaml = new Yaml();
        InputStream data = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(type + ".yml");
        config = yaml.load(data);
        LoggerFactory.getLogger(Config.class).info("Loaded config with " + type + " configuration!");
    }

    public String getType() {
        return type;
    }

    public ConfigDao getConfig() {
        return config;
    }
}
