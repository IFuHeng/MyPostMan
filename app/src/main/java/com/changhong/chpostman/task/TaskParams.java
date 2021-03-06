package com.changhong.chpostman.task;

import java.util.HashMap;


/**
 * @author lds
 */
public class TaskParams {

    private HashMap<String, Object> params;

    public TaskParams() {
        params = new HashMap<>();
    }

    public TaskParams(String key, Object value) {
        this();
        put(key, value);
    }

    public <T> void put(String key, T value) {
        params.put(key, value);
    }

    public <T> T get(String key) {
        return (T) params.get(key);
    }


    public boolean has(String key) {
        return this.params.containsKey(key);
    }

    public String getString(String key) {
        return get(key);
    }
}
