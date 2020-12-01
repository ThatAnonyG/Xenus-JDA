package xyz.xenus.lib.config;

public class ConfigDao {
    private BotDao botDao;
    private MongoDao mongoDao;

    public BotDao getBotDao() {
        return botDao;
    }

    public void setBotDao(BotDao botDao) {
        this.botDao = botDao;
    }

    public MongoDao getMongoDao() {
        return mongoDao;
    }

    public void setMongoDao(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }
}
