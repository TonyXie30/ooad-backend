package com.dormitory.backend.config;

import com.dormitory.backend.pojo.Degree;
import com.dormitory.backend.pojo.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
public class DormKeyGenerator implements KeyGenerator {

    @SuppressWarnings("NullableProblems")
    @Override
    public Object generate(Object target, Method method, Object... params)
    {
        String key = generateKey(method, params);
        log.info("Generated key: {}", key);
        return key;
    }

    public static String generateKey(Method method, Object... params){
        StringBuilder key = new StringBuilder();
        for (Object param : params){
            if (param instanceof Gender){
                param = ((Gender) param).getGender();
            }else if(param instanceof Degree){
                param = ((Degree) param).getDegree();
            }
            key.append(param).append(":");
        }
        key.deleteCharAt(key.length()-1);
        return key.toString();
    }
}
