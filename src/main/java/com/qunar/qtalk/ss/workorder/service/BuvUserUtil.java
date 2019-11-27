package com.qunar.qtalk.ss.workorder.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.*;
import com.qunar.qchat.admin.constants.Config;
import com.qunar.qtalk.ss.utils.HttpClientUtils;
import com.qunar.qtalk.ss.utils.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BuvUserUtil {
    private static final Logger logger = LoggerFactory.getLogger(BuvUserUtil.class);


    public static String getAccessToken() {
//        JSONObject param = new JSONObject();
//        param.put("key", "201910301661154270");
//        param.put("secret", "c90bdf15fe2ff003ca94edb31d042fc0bee5016a");
//        String result = HttpClientUtils.postJson(Config.BUV_ACCESS_TOKEN_URL, param.toJSONString());
//        JSONObject jsonObject = JSONObject.parseObject(result);
//
//        if (jsonObject != null && jsonObject.getIntValue("code") == 10000) {
//            JSONObject dataResult = jsonObject.getJSONObject("result");
//            if (dataResult != null) {
//                String accessToken = dataResult.getString("access_token");
//                logger.info("BuvUserUtil getAccessToken token:{}", accessToken);
//                return accessToken;
//            }
//        }

        LoadingCache<String, String> cached = cached(new CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                System.out.println(System.currentTimeMillis() + "------------");
                return "reload....." + key;
            }
        });
        try {
            String accToken = cached.get("accToken");
            return accToken;
        } catch (ExecutionException e) {
            logger.info("cached get error", e);
        }

        return null;
    }


    public static  <K, V> LoadingCache<K, V> cached(CacheLoader<K, V> cacheLoader) {
        LoadingCache<K, V> cache = CacheBuilder
                .newBuilder()
                .maximumSize(2)
                .weakKeys()
                .softValues()
                .refreshAfterWrite(120, TimeUnit.SECONDS)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener(new RemovalListener<K, V>() {
                    @Override
                    public void onRemoval(RemovalNotification<K, V> rn) {
                        System.out.println(rn.getKey() + "被移除");

                    }
                })
                .build(cacheLoader);
        return cache;
    }
}
