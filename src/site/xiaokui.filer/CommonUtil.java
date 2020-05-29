package site.xiaokui.filer;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static cn.hutool.extra.servlet.ServletUtil.getClientIP;

/**
 * @author HK
 * @date 2019-10-31 13:59
 */
public class CommonUtil {

    private static final String[] IMAGE_SUFFIX = new String[]{"jpg", "jpeg", "bmp", "gif", "png"};

    public static boolean isImage(String fileName) {
        for (String s : IMAGE_SUFFIX) {
            if (fileName.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * windows下面可以使用 ipconfig 查看本机ip，linux下面可以使用 ifconfig 查看本机ip
     */
    public static String getLocalIP() throws SocketException {
        Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (networkInterfaces.hasMoreElements()) {
            // 获取所有网卡
            NetworkInterface netInterface = (NetworkInterface) networkInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                // 找到真实的ip地址
                if (ip instanceof Inet4Address) {
                    return ip.getHostAddress();
                }
            }
        }
        return "127.0.1.1";
    }

    /**
     * 获取客户端ip
     */
    public static String getRemoteIP(HttpServletRequest req) {
        String ip = getClientIP(req);
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static void main(String[] args) throws SocketException {
        System.out.println(getLocalIP());
    }
}
