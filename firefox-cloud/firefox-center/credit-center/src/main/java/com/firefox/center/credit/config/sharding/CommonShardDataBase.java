package com.firefox.center.credit.config.sharding;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liran
 */
public abstract class CommonShardDataBase {

    public static final String DB_SHARD_TIME_FORMAT = "yyyyMM";

    public final static String DB_MAPPING_CONFIG = "shard1:1,shard2:0";

    public static Map<String, String> db = new ConcurrentHashMap<>();

    /**
     * 根据shop id 获取db name
     * 这个是简单的根据分库 字段  shop id分库
     *
     * @param shopId
     * @return
     */
    public String getDatabaseByShopId(String shopId) {
        if (db.size() < 1) {
            String[] split = DB_MAPPING_CONFIG.split(",");
            for (String s : split) {
                String[] split1 = s.split(":");
                db.put(split1[1], split1[0]);
            }
        }
        String s = db.get(shopId);
        return s;
    }
}
