/**
 * Copyright (c) 2014, shouli1990@gmail.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package com.bit.lsm.client.conf;

import com.bit.lsm.client.utils.StringUtils;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： com.bit.lsm.client.conf <br>
 * <b>类名称</b>： MethodConfig <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:shouli1990@gmail.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/2/20 14:12<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class MethodConfig  implements Cloneable {

    public static final String ON = "0";
    public static final String OFF = "1";

    private String methodName;

    private String codePath;

    private String messagePath;

    private String successCode;

    private String failCode;

    private String extendsDataPath;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExtendsDataPath() {
        return extendsDataPath;
    }

    public void setExtendsDataPath(String extendsDataPath) {
        this.extendsDataPath = extendsDataPath;
    }

    public String getFailCode() {
        return failCode;
    }

    public void setFailCode(String failCode) {
        this.failCode = failCode;
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    public String getMessagePath() {
        return messagePath;
    }

    public void setMessagePath(String messagePath) {
        this.messagePath = messagePath;
    }

    public String getCodePath() {
        return codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isDifferent(MethodConfig methodConfig) {
        return methodConfig != null &&
                (!StringUtils.isEquals(this.methodName,methodConfig.methodName) ||
                !StringUtils.isEquals(this.codePath,methodConfig.codePath) ||
                !StringUtils.isEquals(this.messagePath,methodConfig.messagePath) ||
                !StringUtils.isEquals(this.successCode,methodConfig.successCode) ||
                !StringUtils.isEquals(this.failCode,methodConfig.failCode) ||
                !StringUtils.isEquals(this.extendsDataPath,methodConfig.extendsDataPath) ||
                !StringUtils.isEquals(this.status,methodConfig.status));
    }

    @Override
    public String toString() {
        return "MethodConfig{" +
                "methodName='" + methodName + '\'' +
                ", codePath='" + codePath + '\'' +
                ", msgPath='" + messagePath + '\'' +
                ", succCode='" + successCode + '\'' +
                ", failCode='" + failCode + '\'' +
                ", status='" + status + '\'' +
                ", exDataPath='" + extendsDataPath + '\'' +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
