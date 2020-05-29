package site.xiaokui.filer;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Web容器初始化时后自动初始化本类，先于Servlet的初始化
 * @author HK
 * @date 2019-10-31 09:11
 */
public class WebConfig implements ServletContextListener {

    private static Logger logger = LogManager.getLogger(WebConfig.class);

    public static String fileUploadDir, shortUrlDir, rootUrl;

    public static Integer MAX_UPLOAD_FILE_SIZE;

    static {
        Properties properties = new Properties();
        InputStream in = FileServlet.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("请检查根目录下是否存在config.properties:" + e.getMessage());
        }

        fileUploadDir = properties.getProperty("file_upload_dir");
        if (fileUploadDir == null) {
            throw new RuntimeException("config.properties下必须包含file_upload_dir属性");
        }
        shortUrlDir = properties.getProperty("short_url_html_path");
        if (shortUrlDir == null) {
            throw new RuntimeException("config.properties下必须包含short_url_html_path属性");
        }
        rootUrl = properties.getProperty("root_url");
        if (rootUrl == null) {
            throw new RuntimeException("config.properties下必须包含root_url属性");
        }
        try {
            MAX_UPLOAD_FILE_SIZE = Integer.parseInt(properties.getProperty("max_upload_file_size")) * 1024 * 1024;
        } catch (NumberFormatException e) {
            throw new RuntimeException("max_upload_file_size必须为数值型");
        }
        createDirIfNeccesarry(fileUploadDir);
        createDirIfNeccesarry(shortUrlDir);
        logger.info("系统初始化上传文件目录: " + fileUploadDir + ",短链接目录:" + shortUrlDir);
    }

    private static void createDirIfNeccesarry(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists() && dir.isFile()) {
            throw new RuntimeException("文件目录名被占用:" + dirPath);
        }
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new RuntimeException("创建文件夹失败，请检查是否有创建权限::" + dirPath);
            }
        }
    }
}
