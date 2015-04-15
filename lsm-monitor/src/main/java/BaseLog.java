/**
 * Copyright (c) 2014, shouli1990@gmail.com|shouli1990@gmail.com. All rights reserved.
 *
 */

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： PACKAGE_NAME <br>
 * <b>类名称</b>： BaseLog <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:shouli1990@gmail.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/3/26 15:31<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public abstract class BaseLog implements Log{
    private String _LOG_FILE;
    private String _LOCK_FILE;
    private Long _INTERVAL;

    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.WriteLock W_LOCK = LOCK.writeLock();
    private static final ReentrantReadWriteLock.ReadLock R_LOCK = LOCK.readLock();

    private FileLock _FILE_LOCK;
    private FileOutputStream _LOCK_HOLD;
    private FileOutputStream _LOG_WRITER;
    private long _ROLLING_TIME = FileUtil.calcRollingTime();
    private final static List<Object> _LOG_ARR = new ArrayList<Object>(1000);

    private static final ThreadPoolExecutor WRITER_THREAD = (ThreadPoolExecutor) Executors.newFixedThreadPool(1, new LogThreadFactory("_LOG-LOG_WRITER"));
    private static final ScheduledExecutorService SCHEDULED = Executors.newScheduledThreadPool(1, new LogThreadFactory("_LOG_HANDLER"));
    private ScheduledFuture<?> FUTURE;

    public void start(){
        stop();
        FUTURE = SCHEDULED.scheduleWithFixedDelay(new RecordTask(),_INTERVAL, _INTERVAL, TimeUnit.MINUTES);
    }

    public void stop(){
        try {
            ScheduledFuture<?> timer = FUTURE;
            if (timer != null && !timer.isCancelled()) {
                timer.cancel(true);
            }
        } catch (Throwable t) { } finally {
            FUTURE = null;
        }
    }

    private void createFileWriter(String path) throws Exception {
        File log = new File(path);
        File fp = log.getParentFile();

        if(fp != null && !fp.exists()) {
            fp.mkdirs();
        }
        if(log.exists() && log.isFile()) {
            _LOG_FILE = path = FileUtil.getSuitableFileName(path);
        }
        if(log.isFile() && log.lastModified() < FileUtil.calcToDayTime()) {
            FileUtil.rollingFile(path,log.lastModified());
        }

        _LOG_WRITER = new FileOutputStream(path, true);
        _LOCK_FILE = _LOG_FILE + ".lock";
        _LOCK_HOLD = new FileOutputStream(_LOCK_FILE, true);
        _FILE_LOCK = _LOCK_HOLD.getChannel().lock();
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroy();
    }

    public void destroy() {
        if(_FILE_LOCK != null) {
            try {
                _FILE_LOCK.release();
            } catch (IOException e) {}
        }
        if(_LOCK_HOLD != null) {
            try {
                _LOCK_HOLD.close();
            } catch (IOException e) {}
        }
        if(_LOG_WRITER != null) {
            try {
                _LOG_WRITER.close();
            } catch (IOException e) {}
        }
    }

    class RecordTask implements Runnable{
        private List<Object> list;

        RecordTask(){}

        RecordTask(List<Object> list){
            this.list = list;
        }


        @Override
        public void run() {
            FileInputStream inputStream = null;
            FileChannel fileChannel = null;
            BufferedReader reader = null;
        }
    }
}
