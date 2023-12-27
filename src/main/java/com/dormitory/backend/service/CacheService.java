package com.dormitory.backend.service;

import com.dormitory.backend.pojo.ExchangeApplicationCache;
import com.dormitory.backend.pojo.cache;

public class CacheService {

    public CacheService(){}

    public cache createExchangeApplicationCache(){
        return new ExchangeApplicationCache();
    }

    public void saveExchangeApplicationCache(ExchangeApplicationCache cache){
        cache.save(cache);
    }

}
