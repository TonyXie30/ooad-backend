package com.dormitory.backend.config;

import com.dormitory.backend.pojo.Dormitory;
import com.dormitory.backend.pojo.User;
import com.dormitory.backend.service.DormitoryService;
import com.dormitory.backend.service.UserService;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CaffeineManager {

//    public void setDormitoryService(DormitoryService dormitoryService){
//        CaffeineManager.dormitoryService = dormitoryService;
//    }
    private DormitoryService dormitoryService;

    @Autowired
    @Lazy
    public void setDormitoryService(DormitoryService dormitoryService) {
        this.dormitoryService = dormitoryService;
    }

    private UserService userService;

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    @Bean(name = "stringKeyCacheManager",autowireCandidate = false)
    public CacheManager stringKeyCacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(dormCaffeineCache());
        cacheManager.setCacheLoader(new CacheLoader<>() {
            @Nullable
            @Override
            public Object load(@NonNull Object key) {
                return getDormBySpecification((String) key);
            }
        });
        cacheManager.setCacheNames(new ArrayList<>(Arrays.asList("fetchByCompositeKey")));
        return cacheManager;
    }



    @Bean(name = "dormCacheManager",autowireCandidate = false)
    public CacheManager dormCacheBuilder(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(dormCaffeineCache());
        cacheManager.setCacheLoader(new CacheLoader<>() {
            @Nullable
            @Override
            public Object load(@NonNull Object key) {
                return getDorm((Integer) key);
            }
        });
        cacheManager.setCacheNames(new ArrayList<>(Arrays.asList("fetchById")));
        return cacheManager;
    }

    @Bean(name = "userCacheManager",autowireCandidate = false)
    public CacheManager userCacheBuilder(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(userCaffeineCache());
        cacheManager.setCacheLoader(new CacheLoader<>() {
            @Nullable
            @Override
            public Object load(@NonNull Object key) {
                return getUser((String) key);
            }
        });
        cacheManager.setCacheNames(new ArrayList<>(Arrays.asList("fetchById")));
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return  Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(10, TimeUnit.MINUTES)
//                .weakKeys()
                .recordStats();
    }

    private Caffeine<Object, Object> dormCaffeineCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(750)
                .expireAfterAccess(10, TimeUnit.MINUTES)
//                .weakKeys()
                .recordStats()
                .refreshAfterWrite(2,TimeUnit.SECONDS);
    }

    private Caffeine<Object, Object> userCaffeineCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(5000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
//                .weakKeys()
                .recordStats()
                .refreshAfterWrite(2,TimeUnit.SECONDS);
    }

    private Dormitory getDorm(Integer key) {
        return dormitoryService.findById(key);
    }
    private User getUser(String key) {
        return userService.findByUsernameUnCheck(key);
    }

    private Object getDormBySpecification(String key) {
        String[] temp = key.split(":");
        System.out.println(Arrays.toString(temp));
        for (int i = 0; i < temp.length; i++) {
            if(temp[i].equals("null")){
                temp[i] = null;
            }
        }
        List<Dormitory> temp_dorm = dormitoryService.findByHouseNumAndFloorAndBuildingNameAndLocationAndGenderAndDegree(
                temp[0],temp[1],temp[2],temp[3],temp[4],temp[5]
        );
        System.out.println(temp_dorm);
        List<Dormitory> copy_list = new ArrayList<>();
        for (Dormitory dormitory : temp_dorm) {
            copy_list.add((Dormitory) dormitory.clone());
        }
        return new PageImpl<>(copy_list);
    }
//    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
//
//    @PostConstruct
//    public void afterPropertiesSet() {
//        String filePath = CaffeineManager.class.getClassLoader().getResource("").getPath() + File.separator + "config"
//                + File.separator + "caffeine.properties";
//        Resource resource = new FileSystemResource(filePath);
//        if (!resource.exists()) {
//            return;
//        }
//        Properties props = new Properties();
//        try (InputStream in = resource.getInputStream()) {
//            props.load(in);
//            Enumeration<?> propNames = props.propertyNames();
//            while (propNames.hasMoreElements()) {
//                String caffeineKey = (String) propNames.nextElement();
//                String caffeineSpec = props.getProperty(caffeineKey);
//                CaffeineSpec spec = CaffeineSpec.parse(caffeineSpec);
//                Caffeine caffeine = Caffeine.from(spec);
//                Cache manualCache = caffeine.build();
//                cacheMap.put(caffeineKey, manualCache);
//            }
//        }
//        catch (IOException e) {
//            log.error("Initialize Caffeine failed.", e);
//        }
//    }
}
