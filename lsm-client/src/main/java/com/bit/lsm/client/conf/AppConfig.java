/**
 * Copyright (c) 2014, wylipengming@jd.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package com.bit.lsm.client.conf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： com.bit.lsm.client.conf <br>
 * <b>类名称</b>： AppConfig <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:wylipengming@jd.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/2/20 14:11<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class AppConfig implements Cloneable {

    private String rawConfig;

    private String appName;

    /**
     * key : class 's full path , value : ServiceConfig
     */
    private final Map<String, ServiceConfig> services = new ConcurrentHashMap<String, ServiceConfig>();

    public ServiceConfig getService(String className) {
        return services.get(className);
    }

    public void putService(String className, ServiceConfig config) {
        services.put(className, config);
    }

    public Map<String, ServiceConfig> getServices() {
        return services;
    }

    public void clear() {
        this.services.clear();
    }

    public ServiceConfig remove(String serviceName) {
        return this.services.remove(serviceName);
    }

    public boolean isContain(String serviceName) {
        return this.services.containsKey(serviceName);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    public String getRawConfig() {
        return rawConfig;
    }

    public void setRawConfig(String rawConfig) {
        this.rawConfig = rawConfig;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AppConfig{").append("appName='").append(appName).append('\'')
                .append(", services=[");
        for (Map.Entry<String, ServiceConfig> entry : services.entrySet()) {
            sb.append("{").append(entry.getValue()).append("},");
        }
        sb.deleteCharAt(sb.length() - 1).append("]}");
        return sb.toString();
    }

    @Override
    public AppConfig clone() throws CloneNotSupportedException {
        AppConfig config = (AppConfig) super.clone();
        for (Map.Entry<String, ServiceConfig> entry : this.services.entrySet()) {
            config.putService(entry.getKey(), (ServiceConfig) entry.getValue().clone());
        }
        return config;
    }
}
