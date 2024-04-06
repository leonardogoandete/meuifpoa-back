package service;

import redis.clients.jedis.Jedis;


public class RedisService {

    private final Jedis jedis;

    public RedisService() {
        this.jedis = new Jedis("localhost", 6379); // Conexão padrão ao Redis
    }

    public void setKey(String key, String value) {
        jedis.set(key, value);
    }

    public String getValue(String key) {
        return jedis.get(key);
    }
}
