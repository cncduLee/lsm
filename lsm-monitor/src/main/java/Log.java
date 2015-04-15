/**
 * Copyright (c) 2014, shouli1990@gmail.com|shouli1990@gmail.com. All rights reserved.
 *
 */

import java.util.List;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： PACKAGE_NAME <br>
 * <b>类名称</b>： Log <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:shouli1990@gmail.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/3/26 15:29<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public interface Log {
    /**
     * 写日志
     * @param logs
     */
    void writeLog(String logs);

    /**
     * 写日志
     * @param logs
     */
    void writeLog(List<String> logs);
}
