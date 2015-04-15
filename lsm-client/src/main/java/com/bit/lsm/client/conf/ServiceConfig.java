/**
 * Copyright (c) 2014, shouli1990@gmail.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package com.bit.lsm.client.conf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： com.bit.lsm.client.conf <br>
 * <b>类名称</b>： ServiceConfig <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:shouli1990@gmail.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/2/20 14:11<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class ServiceConfig implements Cloneable {

    public static final int DEL = 0x0001;
    public static final int ADD = 0x0002;
    public static final int MOD = 0x0003;

    private String className;

    private String serviceName;

    private int status;

    /**
     * key:method name,value:methodConfig
     */
    private final Map<String, MethodConfig> methods = new ConcurrentHashMap<String, MethodConfig>();

    public String getClassName() {
        return className;
    }

    public void putMethodConfig(String methodDesc, MethodConfig methodConfig) {
        methods.put(methodDesc, methodConfig);
    }

    public MethodConfig getMethod(String methodDesc) {
        return methods.get(methodDesc);
    }

    public void clear() {
        this.methods.clear();
    }

    public MethodConfig remove(String methodDesc) {
        return this.methods.remove(methodDesc);
    }

    public boolean isContain(String methodDesc) {
        return this.methods.containsKey(methodDesc);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, MethodConfig> getMethods() {
        return methods;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServiceConfig{").append("className='").append(className)
                .append('\'').append(", serviceName='").append(serviceName)
                .append('\'').append(", status='").append(status).append('\'')
                .append(", methods=[");
        for (Map.Entry<String, MethodConfig> entry : methods.entrySet()) {
            sb.append("{").append(entry.getValue()).append("},");
        }
        sb.deleteCharAt(sb.length() - 1).append("]}");
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ServiceConfig config = (ServiceConfig) super.clone();
        for (Map.Entry<String, MethodConfig> entry : this.methods.entrySet()) {
            config.putMethodConfig(entry.getKey(), (MethodConfig) entry.getValue().clone());
        }
        return config;
    }
}
