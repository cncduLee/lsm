/**
 * Copyright (c) 2014, shouli1990@gmail.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package com.bit.lsm.client.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.bit.lsm.client.utils.StringUtils.isEmpty;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： com.bit.lsm.client.utils <br>
 * <b>类名称</b>： NetUtil <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:shouli1990@gmail.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/2/20 14:44<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class NetUtil {

    private static final Logger LOGGER = Logger.getLogger(NetUtil.class.getCanonicalName());

    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public static final String LOCAL_IP = getLocalHost();

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null)
            return LOCAL_ADDRESS;
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null
                && !ANYHOST.equals(name)
                && !LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            LOGGER.warning("Failed to retrieving ip address, " + e.getMessage());
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    LOGGER.warning("Failed to retrieving ip address, " + e.getMessage());
                                }
                            }
                        }
                    } catch (Throwable e) {
                        LOGGER.warning("Failed to retrieving ip address, " + e.getMessage());
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.warning("Failed to retrieving ip address, " + e.getMessage());
        }
        LOGGER.warning("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    public static boolean isConnectAble(String all, int timeout) {
        if (!isEmpty(all) && timeout > 0) {
            try {
                String[] str = all.split("/")[0].split(":", 2);
                return isConnectAble(str[0], Integer.valueOf(str[1]), timeout);
            } catch (Throwable e) {
                //
            }
        }
        return false;
    }

    public static boolean isConnectAble(String host, int port, int timeout) {
        boolean isReachable = false;
        Socket socket = null;
        try {
            socket = new Socket();
            /*// 端口号设置为 0 表示在本地挑选一个可用端口进行连接
            SocketAddress localSocketAddr = new InetSocketAddress(getLocalAddress(), 0);
            socket.bind(localSocketAddr);*/
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(host, port);
            socket.connect(endpointSocketAddr, timeout);
            isReachable = true;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Throwable e) {
                    //
                }
            }
        }
        return isReachable;
    }
}