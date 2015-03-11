/**
 * Copyright (c) 2014, wylipengming@jd.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package com.bit.lsm.client;

import com.alibaba.dubbo.registry.NotifyListener;
import com.bit.lsm.client.conf.AppConfig;
import com.bit.lsm.client.conf.MethodConfig;
import com.bit.lsm.client.conf.ServiceConfig;
import com.bit.lsm.client.utils.AgentThreadFactory;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bit.lsm.client.utils.NetUtil.isConnectAble;
import static com.bit.lsm.client.utils.StringUtils.isEmpty;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： com.bit.lsm.client <br>
 * <b>类名称</b>： Config <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:wylipengming@jd.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/2/20 14:41<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class Config {

    private static final Logger LOGGER = Logger.getLogger(Config.class.getCanonicalName());

    /**
     * debug class generate path
     */
    public static final String CLASS_DEBUG_PATH = System.getProperty("class.debug");
    /**
     * initialize ID 's counts
     */
    public static final int ID_INIT_COUNT = Integer.valueOf(System.getProperty("id.init", "100000"));
    /**
     * ID generator guard thread interval times (ms)
     */
    public static final int ID_INTERVAL = Integer.valueOf(System.getProperty("id.interval", "3000"));
    /**
     * log task queue size
     */
    public static final int BUFFER_SIZE = 2 << Integer.valueOf(System.getProperty("buffer.size.pow", "12"));

    /**
     * record log thread numbers
     */
    public static final int THREAD_NUMBER = Integer.valueOf(System.getProperty("log.threads", Math.min(4, Runtime.getRuntime().availableProcessors()) + ""));
    /**
     * log path
     */
    public static final String LOG_PATH = System.getProperty("log.path", "/export/log/sgm-agent/sgm-agent_access.log");
    /**
     * send log interval time (ms)
     */
    public static final String LOG_SEND_INTERVAL_MS = System.getProperty("send.interval.ms", "100");
    /**
     * queue
     */
    public static final String SEND_TIME_OUT = System.getProperty("send.timeout", "3000");
    /**
     * send time out (ms)
     */
    public static final String LOG_SEND_QUEUE = System.getProperty("send.queue", "100000");
    /**
     * queue.enqueue.timeout.ms
     */
    public static final String QUEUE_ENQUEUE_TIMEOUT = System.getProperty("queue.enqueue.timeout", "0");
    /**
     * batch send log number
     */
    public static final String LOG_SEND_BATCH = System.getProperty("send.batch.num", "1000");
    /**
     * message center addresses
     */
    public static final String MSG_SERVER_ADDRS = System.getProperty("sgm.msg.servers", System.getenv("SGM_MSG_SERVERS"));
    /**
     * default message center addrs config
     */
    public static final String DEFAULT_MSG_SERVER_ADDRS = "sgm-msg1.d.chinabank.com.cn:9092,sgm-msg2.d.chinabank.com.cn:9092,sgm-msg3.d.chinabank.com.cn:9092";
    /**
     * configuration server addr
     */
    public static final String CONFIG_CENTER_ADDR = System.getProperty("config.center.addr", System.getenv("CONFIG_CENTER_ADDR"));
    /**
     * default configuration server addr
     */
    public static final String DEFAULT_CONFIG_CENTER_ADDR = "sgm-admin.cbpmgt.com:80";

    public static final String CONFIG_CENTER_SERVICE_URL = "http://" + (isEmpty(CONFIG_CENTER_ADDR) ? DEFAULT_CONFIG_CENTER_ADDR : CONFIG_CENTER_ADDR) + "/query/queryAgentConfig.ac?application=";

    static final String TOPIC = "sgm-log";

    public static final String APP_NAME;

    public static AppConfig APP_CONFIG;

    public static final String CLASS_PATH = "java.class.path";

    public static final Pattern APP_PATH_PATTERN = Pattern.compile("[^/\\\\]+(?=[/\\\\]?(target|bin|conf|lib|WEB-INF)[/\\\\])", Pattern.CASE_INSENSITIVE);

    public static final Pattern APP_NAME_PATTERN = Pattern.compile("\\s*<property\\s+name=\"APP\"\\s+value=\"(.+)\"\\s*/>\\s*", Pattern.CASE_INSENSITIVE);

    public static final Pattern APP_PATH_TOMCAT_PATTERN = Pattern.compile("\\s*<Context[\\s\\S]*docBase=\"([^\"]+)\"[\\s\\S]*", Pattern.CASE_INSENSITIVE);

    private static boolean available = true;

    public static final String CACHE_FILE = System.getProperty("user.home") + File.separatorChar + ".sgm" + File.separatorChar + "sgm-config.cache";

    public static final String LOGBACK_PATH = "logback.xml";

    private static final List<NotifyListener> listeners = new CopyOnWriteArrayList();

    /**
     * sync app config interval time (min)
     */
    public static final String SYNC_APP_CONFIG_INTERVAL = System.getProperty("app.config.interval", System.getenv("APP_CONFIG_INTERVAL"));
    public static final int SYNC_APP_CONFIG_INTERVAL_DEFAULT = 5;

    public static List<File> CLASS_PATH_ARRAY_EXCEPT_JRE = getClassPath();

    static {
        String app_name = System.getProperty("app.name", System.getenv("APP_NAME"));
        LOGGER.info("app name by env : " + app_name);
        if (isEmpty(app_name)) {
            URL logConfigUrl = gerConfigByResource();
            if (logConfigUrl != null) {
                try {
                    app_name = readXmlByPattern(logConfigUrl.openStream(), APP_NAME_PATTERN);
                    LOGGER.info("app name by root path : " + app_name);
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "app name by root path error : ", e);
                }
            }
            if (isEmpty(app_name)) {
                URL tmp = getLogConfigUrl();
                if (tmp != null) {
                    try {
                        logConfigUrl = tmp;
                        app_name = readXmlByPattern(tmp.openStream(), APP_NAME_PATTERN);
                        LOGGER.info("app name by search : " + app_name);
                    } catch (Throwable e) {
                        LOGGER.log(Level.WARNING, "get app name error :", e);
                    }
                }
            }
            if (logConfigUrl != null && !isEmpty(app_name)) {
                //set logger
                String filePath = logConfigUrl.toString();
                System.setProperty("logback.configurationFile", filePath);
                LOGGER.info("set logger config path :" + filePath);
                //set log level of kafka.producer.async.ProducerSendThread 's logger
                processingLogLevel();
            }
        }
        if (isEmpty(app_name))
            available = false;

        APP_NAME = app_name;
        AppConfig tmp = parseConfig(getAppConfig());
        //local cache
        APP_CONFIG = localCacheHandle(tmp);
    }

    private static void processingLogLevel() {
        try {
            org.slf4j.Logger logger = LoggerFactory.getLogger("kafka.producer.async.ProducerSendThread");
            if (logger.isDebugEnabled()) {
                if (logger instanceof ch.qos.logback.classic.Logger) {
                    ((ch.qos.logback.classic.Logger) logger).setLevel(ch.qos.logback.classic.Level.INFO);
                }
            }
        } catch (Throwable e) {
            //ignore
        }
    }

    private static URL gerConfigByResource() {
        ClassLoader classLoader = AgentMain.class.getClassLoader();
        if (classLoader == null)
            classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(LOGBACK_PATH);  //
    }

    private static AppConfig localCacheHandle(AppConfig appConfig) {
        AppConfig tmp = appConfig;
        try {
            File file = new File(CACHE_FILE);
            if (appConfig == null) {
                //get config by local cache
                if (file.exists() && file.isFile()) {
                    FileInputStream fileInputStream = null;
                    try {
                        Properties properties = new Properties();
                        fileInputStream = new FileInputStream(file);
                        properties.load(fileInputStream);
                        String appConfigStr = properties.getProperty("current");
                        if ((tmp = parseConfig(appConfigStr)) == null) {
                            LOGGER.warning("get local cache of current fail : " + appConfigStr);
                            String history = properties.getProperty("history");
                            if ((tmp = parseConfig(history)) == null) {
                                LOGGER.warning("get local cache of history fail : " + history);
                            } else {
                                LOGGER.warning("get local cache of history succeed : " + history);
                            }
                        } else {
                            LOGGER.warning("get local cache of current succeed : " + appConfigStr);
                        }
                    } finally {
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    }
                }
            } else {
                //update local cache
                FileOutputStream fileOutputStream = null;
                FileInputStream fileInputStream = null;
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    Properties properties = new Properties();
                    String current = appConfig.getRawConfig();
                    if (file.exists() && file.isFile()) {
                        fileInputStream = new FileInputStream(file);
                        properties.load(fileInputStream);
                        String oldCurrent = properties.getProperty("current");
                        if (!isEmpty(current) && current.equalsIgnoreCase(oldCurrent)) {
                            return tmp;
                        }
                        String beforeHistory = properties.getProperty("history");
                        if (!isEmpty(beforeHistory))
                            LOGGER.info("local cache history was overwrite : " + beforeHistory);
                        properties.setProperty("history", isEmpty(oldCurrent) ? "" : oldCurrent);
                    } else {
                        file.createNewFile();
                    }
                    properties.setProperty("current", current);
                    fileOutputStream = new FileOutputStream(file);
                    properties.store(fileOutputStream, tmp.getAppName() + " 's cache");
                    fileOutputStream.flush();
                    LOGGER.info("update local cache succeed !");
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            //
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            //
                        }
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "handle local cache error : ", e);
        }
        return tmp;  //
    }

    public static boolean isAvailable() {
        return available;
    }

    private static String getAppConfig() {
        if (!available)
            return null;
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        try {
            String host = isEmpty(CONFIG_CENTER_ADDR) ? DEFAULT_CONFIG_CENTER_ADDR : CONFIG_CENTER_ADDR.split("/")[0];
            if (!isConnectAble(host, 5000)) {
                LOGGER.warning("config center [" + host + "] not connect able at 5s timeout!");
                return null;
            }
            String url = CONFIG_CENTER_SERVICE_URL + APP_NAME;
            LOGGER.info("get app config url : " + url);
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                String[] hosts = host.split(":", 2);
                sm.checkConnect(hosts[0], hosts.length == 1 ? 80 : Integer.valueOf(hosts[1]));
            }
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String lines;
            while ((lines = reader.readLine()) != null) {
                sb.append(lines);
            }
            return sb.toString();
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "get app config error : ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable e) {
                    //
                }
            }
            if (connection != null) {
                try {
                    // 断开连接
                    connection.disconnect();
                } catch (Throwable e) {
                    //
                }
            }
        }
        return null;  //
    }

    private static AppConfig parseConfig(String s) {
        LOGGER.info("get config : " + s);
        if (isEmpty(s) || s.length() <= 2)
            return null;
        AppConfig appConfig = new AppConfig();
        try {
            appConfig.setRawConfig(s);
            s = s.replace("[", "").replace("]", "");
            String[] servicesStr = s.split("\",\"");
            for (String serString : servicesStr) {
                serString = serString.replaceAll("\"", "");
                String[] keyValues = serString.split(";");
                Map<String, String> pairs = new HashMap<String, String>();
                for (String value : keyValues) {
                    String[] pair = value.split(":", 2);
                    pairs.put(pair[0], pair[1]);
                }
                String appName = pairs.get("appName");
                String serviceName = pairs.get("serviceName");
                String className = pairs.get("className");
                String methodName = pairs.get("methodName").replaceAll("\\s*", "");
                String codePath = pairs.get("codePath");
                String succCode = pairs.get("succCode");
                String failCode = pairs.get("failCode");
                String msgPath = pairs.get("msgPath");
                String status = pairs.get("status");
                String exData = pairs.get("exData");
                appConfig.setAppName(appName);
                ServiceConfig serviceConfig = appConfig.getService(className);
                if (serviceConfig == null) {
                    serviceConfig = new ServiceConfig();
                    appConfig.putService(className, serviceConfig);
                }
                serviceConfig.setServiceName(serviceName);
                serviceConfig.setClassName(className);
                MethodConfig methodConfig = serviceConfig.getMethod(methodName);
                if (methodConfig == null) {
                    methodConfig = new MethodConfig();
                    serviceConfig.putMethodConfig(methodName, methodConfig);
                }
                methodConfig.setMethodName(methodName);
                methodConfig.setCodePath(codePath);
                methodConfig.setFailCode(failCode);
                methodConfig.setMessagePath(msgPath);
                methodConfig.setStatus(status);
                methodConfig.setSuccessCode(succCode);
                methodConfig.setExtendsDataPath(exData);
            }
            LOGGER.info(appConfig.toString());
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "parse app config error : ", e);
            return null;
        }
        return appConfig;
    }

    private static List<File> getClassPath() {
        List<File> ret = new ArrayList<File>();
        String[] paths = System.getProperty(CLASS_PATH).split(File.pathSeparator);
        String java_home = File.separator + "jre" + File.separator + "lib";
        for (String path : paths) {
            if (!path.contains(java_home)) {
                ret.add(new File(path));
            }
        }
        //兼容tomcat
        String app_home = System.getProperty("catalina.base", System.getenv("catalina.base"));
        if (!isEmpty(app_home)) {
            URL url = getConfigFileRecursion(new File(app_home), "server.xml");
            String path = null;
            try {
                if (url != null)
                    path = readXmlByPattern(url.openStream(), APP_PATH_TOMCAT_PATTERN);
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "get tomcat docBase error :", e);
            }
            if (!isEmpty(path)) {
                ret.add(new File(path));
            }
            ret.add(new File(app_home));
            LOGGER.info("tomcat app home : " + app_home);
        }
        return ret;
    }

    private static URL getLogConfigUrl() {
        URL url;
        for (File file : CLASS_PATH_ARRAY_EXCEPT_JRE) {
            if (!file.exists()) {
                continue;
            }
            if (file.isDirectory()) {
                //dir
                url = getConfigFileRecursion(file, LOGBACK_PATH);
            } else {
                //jar
                url = getFileByJar(file, LOGBACK_PATH);
            }
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    /**
     * 获取jar中配置文件应用名
     *
     * @param jarFile java.io.File 文件File
     * @param fName   xml名称
     * @return prefix file URL
     */
    private static URL getFileByJar(File jarFile, String fName) {
        URL url;
        //jar
        JarFile fis = null;
        try {
            fis = new JarFile(jarFile);
            Enumeration<JarEntry> enumeration = fis.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry e = enumeration.nextElement();
                String eName = e.getName();
                if (!eName.endsWith("/")) {
                    int index = eName.lastIndexOf("/");
                    String entryName = index == -1 ? eName : eName.substring(index + 1);
                    if (entryName.equalsIgnoreCase(fName)) {
                        url = new URL("jar:file:/" + jarFile.getAbsolutePath() + "!/" + eName);
                        LOGGER.info("found " + fName + " in jar : " + url.toString());
//                        String appName = readXmlByPattern(fis.getInputStream(e), pattern);
                        return url;
                    }
                }
            }
        } catch (Throwable e1) {
            //ignore
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return null;
    }

    /**
     * 从xml文件中获取Pattern中的数据，eg：<property name="APP" value="xxx"/>
     *
     * @param inputStream 文件输入流
     * @return 应用名称
     * @throws IOException
     */
    private static String readXmlByPattern(InputStream inputStream, Pattern pattern) {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    return matcher.group(1);
                }
            }
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "read xml error : ", e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return null;
    }

    /**
     * 递归文件夹获取prefix.xml文件
     *
     * @param dir   文件夹
     * @param fName 文件名
     * @return URL
     */
    private static URL getConfigFileRecursion(File dir, String fName) {
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        URL url = null;
        for (File f : files) {
            try {
                if (f.isDirectory()) {
                    url = getConfigFileRecursion(f, fName);
                } else {
                    if (f.getName().equalsIgnoreCase(fName)) {
                        LOGGER.info("found " + fName + " : " + f.getAbsolutePath());
//                        appName = readXmlByPattern(new FileInputStream(f), pattern);
                        url = f.toURI().toURL();
                    } else if (f.getName().endsWith(".jar")) {
                        url = getFileByJar(f, fName);
                    }
                }
                if (url != null) {
                    return url;
                }
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "getConfigFileRecursion error : ", e);
            }
        }
        return null;
    }

    private static String getAppNameByPattern() {
        for (File file : CLASS_PATH_ARRAY_EXCEPT_JRE) {
            Matcher matcher = APP_PATH_PATTERN.matcher(file.getAbsolutePath());
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    private static final ScheduledExecutorService SCHEDULED = Executors.newScheduledThreadPool(1, new AgentThreadFactory("sync-app-config"));

    private static final ExecutorService NOTIFY_SERVICE = Executors.newFixedThreadPool(1, new AgentThreadFactory("sync-app-notify"));

    private static ScheduledFuture<?> scheduledFuture;

    protected static void startSyncConfig() {
        if (AgentMain.isAgent()) {
            stopSyncConfig();
            int interval;
            try {
                interval = isEmpty(SYNC_APP_CONFIG_INTERVAL) ? SYNC_APP_CONFIG_INTERVAL_DEFAULT : Integer.valueOf(SYNC_APP_CONFIG_INTERVAL);
            } catch (NumberFormatException e) {
                interval = SYNC_APP_CONFIG_INTERVAL_DEFAULT;
                LOGGER.log(Level.WARNING, "app.config.interval not a integer :" + e.getMessage());
            }
            scheduledFuture = SCHEDULED.scheduleWithFixedDelay(new SyncTask(), interval, interval, TimeUnit.MINUTES);
        }
    }

    protected static void stopSyncConfig() {
        try {
            ScheduledFuture<?> timer = scheduledFuture;
            if (timer != null && !timer.isCancelled()) {
                timer.cancel(true);
            }
            listeners.clear();
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING, t.getMessage(), t);
        } finally {
            scheduledFuture = null;
        }
    }

    private static class SyncTask implements Runnable {

        @Override
        public void run() {
            try {
                String appStr = getAppConfig();
                if (!isEmpty(appStr) || !"[]".equals(appStr)) {
                    if (APP_CONFIG == null || !APP_CONFIG.getRawConfig().equals(appStr)) {
                        final AppConfig appConfig = parseConfig(appStr);
                        if (appConfig == null)
                            return;
                        try {
                            NOTIFY_SERVICE.submit(new Runnable() {
                                @Override
                                public void run() {
                                    changedNotify(appConfig);
                                }
                            });
                        } catch (Throwable e) {
                            LOGGER.log(Level.WARNING, "notify app config error:", e);
                        }
                        AppConfig changed = appConfig.clone();
                        getModified(APP_CONFIG, changed);
                        LOGGER.info("Incremental change :" + changed.toString());
                        AgentMain.getClassTransformer().redefineClasses(changed);
                        localCacheHandle(appConfig);
                        APP_CONFIG = appConfig;
                    }
                }
            } catch (Throwable t) {
                LOGGER.log(Level.WARNING, "sync app config error:", t);
            }
        }
    }

    private static void getModified(AppConfig src, AppConfig appConfig) throws CloneNotSupportedException {
        if (src == null || appConfig == null)
            return;
        Map<String, ServiceConfig> srcServices = src.getServices();
        Map<String, ServiceConfig> services = appConfig.getServices();
        for (Map.Entry<String, ServiceConfig> entry : services.entrySet()) {
            String key = entry.getKey();
            ServiceConfig sc = entry.getValue();
            getModified(srcServices.get(key), sc);
        }
        for (Map.Entry<String, ServiceConfig> entry : srcServices.entrySet()) {
            String key = entry.getKey();
            ServiceConfig sc = entry.getValue();
            if (!services.containsKey(key)) {
                ServiceConfig tmp = (ServiceConfig) sc.clone();
                tmp.setStatus(DEL);
                services.put(key, tmp);
            }
        }
    }

    private static void getModified(final ServiceConfig srcConfig, ServiceConfig config) throws CloneNotSupportedException {
        if (srcConfig == null || config == null) {
            if (config != null)
                config.setStatus(ADD);
            return;
        }
        boolean isEquals = isEquals(srcConfig.getServiceName(), config.getServiceName());
        Map<String, MethodConfig> srcMConfig = srcConfig.getMethods();
        Map<String, MethodConfig> mConfig = config.getMethods();

        for (Map.Entry<String, MethodConfig> entry : srcMConfig.entrySet()) {
            String key = entry.getKey();
            MethodConfig srcM = entry.getValue();
            if (!mConfig.containsKey(key)) {
                MethodConfig clone = (MethodConfig) srcM.clone();
                clone.setStatus(OFF);
                mConfig.put(key, clone);
            }
        }

        for (Map.Entry<String, MethodConfig> entry : mConfig.entrySet()) {
            String key = entry.getKey();
            MethodConfig mc = entry.getValue();
            MethodConfig srcM = srcMConfig.get(key);
            if (srcM != null) {
                if (!srcM.isDifferent(mc) && isEquals) {
                    //no change
                    mConfig.remove(key);
                }
            }
        }
        if (mConfig.size() > 0 || !isEquals)
            config.setStatus(MOD);
//        return config;
    }

    public static void addListener(NotifyListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
        LOGGER.log(Level.INFO, "add notify listener :", listener);
    }

    private static void changedNotify(AppConfig appConfig) {
        for (NotifyListener listener : listeners) {
            try {
                listener.notify(appConfig.clone());
            } catch (Throwable t) {
                LOGGER.log(Level.WARNING, "AppConfig changed notify[" + listener + "] error:", t);
            }
        }
    }
}