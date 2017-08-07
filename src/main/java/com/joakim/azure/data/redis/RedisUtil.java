package com.joakim.azure.data.redis;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import com.joakim.azure.Config;
import com.joakim.azure.EnvVarNames;

/**
 * This class implements Azure RedisCache operations.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/07/20
 */

public class RedisUtil implements EnvVarNames {

// Constants:
  private final static Logger logger = Logger.getLogger(RedisUtil.class);
  
  // Instance variables:
  String         namespace = null;
  String         redisHost = null;
  String         redisKey  = null;
  JedisShardInfo shardInfo = null;
  Jedis          jedis     = null;
  
  /**
   * Default constructor; config values come from environment variables.
   */
  public RedisUtil() {

    super();
        
    namespace = Config.envVar(EnvVarNames.AZURE_REDISCACHE_NAMESPACE);
    redisHost = String.format("%s.redis.cache.windows.net", namespace);
    redisKey  = Config.envVar(EnvVarNames.AZURE_REDISCACHE_KEY);
    
    shardInfo = new JedisShardInfo(redisHost, 6379);
    shardInfo.setPassword(redisKey);
    jedis = new Jedis(shardInfo);
    logger.debug("connected to: " + redisHost);
  }
  
  public String set(String key, String value) {
    
    return jedis.set(key, value);
  }
  
  public String get(String key) {
    
    return jedis.get(key);
  }

}
