package site.xiaokui.filer;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author HK
 * @date 2019-10-31 09:11
 */
public class Config {

    private static Logger logger = LogManager.getLogger(Config.class);

    public static final String ROOT_DIR;

    public static Integer MAX_UPLOAD_FILE_SIZE;

    static {
        Properties properties = new Properties();
        InputStream in = FileServlet.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("请检查根目录下是否存在config.properties:" + e.getMessage());
        }

        ROOT_DIR = properties.getProperty("img_save_path");
        if (ROOT_DIR == null) {
            throw new RuntimeException("config.properties下必须包含img_save_path属性");
        }
        try {
            MAX_UPLOAD_FILE_SIZE = Integer.parseInt(properties.getProperty("max_upload_file_size")) * 1024 * 1024;
        } catch (NumberFormatException e) {
            throw new RuntimeException("max_upload_file_size必须为数值型");
        }

        File dir = new File(ROOT_DIR);
        if (dir.exists() && dir.isFile()) {
            throw new RuntimeException("文件目录名被占用:" + ROOT_DIR);
        }
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new RuntimeException("创建文件夹失败，请检查是否有创建权限::" + ROOT_DIR);
            }
        }
        logger.info("系统初始化文件存储根目录为：" + ROOT_DIR);
    }

    private Config() {
    }
}
