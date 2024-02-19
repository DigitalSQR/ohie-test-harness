package com.argusoft.path.tht.systemconfiguration.utils;

import io.swagger.annotations.ApiParam;

import java.lang.reflect.Field;

public class RestControllerUtils {
    public static <T> T filterFields(T filter, Class<T> filterClass) {
        try {
            T filteredFilter = filterClass.getDeclaredConstructor().newInstance();

            for (Field field : filterClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ApiParam.class)) {
                    field.setAccessible(true);
                    Object value = field.get(filter);
                    field.set(filteredFilter, value);
                }
            }
            return filteredFilter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
