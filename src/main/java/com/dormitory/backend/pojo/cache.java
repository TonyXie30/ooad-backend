package com.dormitory.backend.pojo;

import org.hibernate.annotations.Cache;
import org.springframework.stereotype.Component;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Component
public class cache<T> {
    private final ConcurrentHashMap<String, SoftReference<T>> CACHE
            = new ConcurrentHashMap<>();

    private final DelayQueue<DelayedCacheEntity<T>> DELAYED_QUEUE
            = new DelayQueue<>();

    public cache(){
        Thread cleanThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()){
                try{
                    DelayedCacheEntity<T> entity = DELAYED_QUEUE.take();
                    CACHE.remove(entity.getKey());
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                }catch(Exception ignore){
                    //Ignore
                }
            }
        });
        cleanThread.setDaemon(true);
        cleanThread.start();
    }

    public void add(String key, T value, long expirePeriodInMillis) {
        if (key==null) return;
        if (value==null) CACHE.remove(key); //Alternative to exception.

        long expiryTime = System.currentTimeMillis() + expirePeriodInMillis;
        SoftReference<T> reference = new SoftReference<>(value);
        CACHE.put(key, reference);
        DELAYED_QUEUE.put(new DelayedCacheEntity<T>(key, reference, expiryTime));
    }

    public T get(String key) {
        SoftReference<T> referenceObject = CACHE.get(key);
        return referenceObject == null ? null : referenceObject.get();
    }

    public ConcurrentHashMap<String, SoftReference<T>> getCache() {
        return CACHE;
    }

    public void remove(String key) {
        CACHE.remove(key);
    }
    private static class DelayedCacheEntity<T> implements Delayed {
        private final String key;
        private final SoftReference<T> referenceObject;
        private final long expiryTime;

        public DelayedCacheEntity(String key, SoftReference<T> referenceObject, long expiryTime) {
            this.key = key;
            this.referenceObject = referenceObject;
            this.expiryTime = expiryTime;
        }

        public String getKey() {
            return key;
        }

        public SoftReference<T> getReferenceObject() {
            return referenceObject;
        }
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expiryTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(expiryTime,((DelayedCacheEntity<?>) o).expiryTime);
        }
    }
}
