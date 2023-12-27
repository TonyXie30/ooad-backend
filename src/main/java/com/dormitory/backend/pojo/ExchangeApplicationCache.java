package com.dormitory.backend.pojo;

import com.dormitory.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExchangeApplicationCache extends cache<Set<String>>{
    @Autowired
    UserService userService;
    public ExchangeApplicationCache(){super();}

    public void save(ExchangeApplicationCache cache) {
        ConcurrentHashMap<String, SoftReference<Set<String>>> exchangeApplicationCache = cache.getCache();
        exchangeApplicationCache.forEach((usr,from_)->{
            Set<String> fromSet = from_.get();
            user userInDB = userService.findByUsername(usr);
            Set<user> fromSetToDB = new HashSet<>();
            if (fromSet != null) {
                fromSet.forEach(from->{
                    fromSetToDB.add(userService.findByUsername(from));
                });
            }
            userInDB.setExchangeApplication(fromSetToDB);
            userService.register(userInDB);
        });
    }

}
