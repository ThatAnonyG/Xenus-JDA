package xyz.xenus.lib.config;

import java.util.ArrayList;

public class ConfigDao {
    private BotDao botDao;
    private TokensDao tokenDao;
    private MongoDao mongoDao;
    private ArrayList<NodeDao> nodeDao;

    public BotDao getBotDao() {
        return botDao;
    }

    public void setBotDao(BotDao botDao) {
        this.botDao = botDao;
    }

    public TokensDao getTokenDao() {
        return tokenDao;
    }

    public void setTokenDao(TokensDao tokensDao) {
        this.tokenDao = tokensDao;
    }

    public MongoDao getMongoDao() {
        return mongoDao;
    }

    public void setMongoDao(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    public ArrayList<NodeDao> getNodeDao() {
        return nodeDao;
    }

    public void setNodeDao(ArrayList<NodeDao> nodeDao) {
        this.nodeDao = nodeDao;
    }
}
