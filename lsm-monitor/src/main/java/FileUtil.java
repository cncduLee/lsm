/**
 * Copyright (c) 2014, shouli1990@gmail.com|shouli1990@gmail.com. All rights reserved.
 *
 */

import com.wangyin.commons.util.Logger;
import com.wangyin.commons.util.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * <b>项目名</b>： wallet-customer <br>
 * <b>包名称</b>： com.wangyin.customer.biz.cert.emend.log <br>
 * <b>类名称</b>： FileUtil <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:shouli1990@gmail.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/3/26 15:19<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class FileUtil {
    private static final ReentrantLock LOCK = new ReentrantLock();
    private long rollingTime;
    private FileOutputStream writer;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\r\n");
    private static String LOG_PATH ;
    private FileLock fileLock;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
    private static final String LOG_FILE_PATTERN = "([^/\\.\\\\-]+)-?\\d*(?=\\.[^.]+)";

    private static final ThreadLocal<SimpleDateFormat> TIME_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    public static String rollingFile(String filePath, long time) throws IOException {
        File logFile = new File(filePath);
        File rollingFile = new File(filePath + "." + TIME_FORMAT.get().format(new Date(time)));
        if (!logFile.renameTo(rollingFile)) {
            throw new IOException("fail log rolling error : [" + logFile.getCanonicalPath() + "] rename to [" + rollingFile.getCanonicalPath() + "] fail!");
        }
        return rollingFile.getAbsolutePath();
    }

    private void rollingFile(long time) throws IOException {
        this.close();
        File logFile = new File(LOG_PATH);
        File rollingFile = new File(LOG_PATH + "." + TIME_FORMAT.get().format(new Date(time)));
        if (!logFile.renameTo(rollingFile)) {}
        rollingTime = calcRollingTime();
    }

    public void close() {
        if (fileLock != null) {
            try {
                fileLock.release();
            } catch (IOException e) {}
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {}
        }
    }


    public static String getSuitableFileName(String path) throws Exception {
        String tmp = path;
        int count = 0;
        boolean flag = false;
        do {
            if (count == 30) {
                throw new Exception("too many log file over 30 !");
            }
            String lockFile = tmp + ".lock";
            FileOutputStream fis = null;
            FileLock lock = null;
            try {
                fis = new FileOutputStream(lockFile, true);
                FileChannel fc = fis.getChannel();
                lock = fc.tryLock();
                if (lock != null && lock.isValid() && !lock.isShared()) {
                    flag = true;
                } else {
                    LOGGER.info("can't get [", lockFile, "] file lock:", lock);
                    tmp = tmp.replaceFirst(LOG_FILE_PATTERN, "$1-" + count++);
                }
            } catch (Exception e) {
                LOGGER.warn("open [", lockFile, "] log file error:", e);
                tmp = tmp.replaceFirst(LOG_FILE_PATTERN, "$1-" + count++);
            } finally {
                if (lock != null) {
                    try {
                        lock.release();
                    } catch (IOException e) { }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {}
                }
            }
        } while (!flag);
        return tmp;
    }

    public void write(String log, long time) throws IOException {
        LOCK.lock();
        try {
            if (time >= rollingTime) {
                rollingFile(rollingTime - 1000);
                writer = new FileOutputStream(LOG_PATH, true);
                fileLock = writer.getChannel().lock();
            }
            writer.write((log + LINE_SEPARATOR).getBytes());
        } catch (IOException e) {} finally {
            LOCK.unlock();
        }
    }

    public static long calcRollingTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long calcToDayTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
