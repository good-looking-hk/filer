package site.xiaokui.filer;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传
 * @author HK
 * @date 2019-10-30 17:55
 */
@WebServlet(urlPatterns = "/upload")
public class UploadServlet extends HttpServlet {

    private static Logger logger = LogManager.getLogger(UploadServlet.class);

    private static String rootDir = WebConfig.fileUploadDir;

    private static final int MAX_FILE_SIZE = WebConfig.MAX_UPLOAD_FILE_SIZE;

    private static final String SLASH = File.separator;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=utf-8");
        // 不能使用form表单，需要ajax配合，否则dir总是null
        // 另外一种方法就是使用post，后面接?dir={dir}
        String dir = req.getParameter("dir");
        PrintWriter pw = resp.getWriter();
        try {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
            upload.setSizeMax(MAX_FILE_SIZE);
            List<FileItem> fileItems = upload.parseRequest(req);
            if (fileItems.size() == 0) {
                ResultEntity resultEntity = ResultEntity.failed("上传文件未指定name");
                pw.print(resultEntity);
                return;
            }
            for (FileItem fileItem : fileItems) {
                if (!fileItem.isFormField()) {
                    processUploadFile(fileItem, dir, pw);
                }
            }
        } catch (Exception e) {
            String resultEntity = ResultEntity.error("上传文件失败，异常信息：" + e.getMessage()).toString();
            pw.print(resultEntity);
            logger.error("解析文件失败，异常信息:", e);
        } finally {
            pw.close();
        }
    }

    private void processUploadFile(FileItem item, String dir, PrintWriter pw) throws Exception {
        String fileName = item.getName();
        long fileSize = item.getSize();
        if ("".equals(fileName) && fileSize == 0) {
            return;
        }
        int index = fileName.lastIndexOf('.');
        String suffix = fileName.substring(index);
        // fileName = newFileName(fileName);
        fileName = UUID.randomUUID().toString().replaceAll("-","") + suffix;
        Date date = new Date();
        String year = new SimpleDateFormat("yyyy").format(date);
        String month = new SimpleDateFormat("MM").format(date);
        String day = new SimpleDateFormat("dd").format(date);
        File uploadFile;
        if (dir == null || "".equals(dir)) {
            fileName = SLASH + year + SLASH + month + SLASH + day + SLASH + fileName;
            uploadFile = new File(rootDir + fileName);
        } else {
            fileName = SLASH + dir + SLASH + year + SLASH + month + SLASH + day + SLASH + fileName;
            uploadFile = new File(rootDir + fileName);
        }

        File parent = uploadFile.getParentFile();
        // 请确保有创建权限
        if (!parent.exists()) {
            boolean success = parent.mkdirs();
            logger.info("创建目录：" + parent + ":" + success);
            if (!success) {
                Thread.sleep(150);
                // 避免一些可能发生的潜在问题
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new RuntimeException("创建文件夹失败：" + parent.getAbsolutePath());
                }
            }
        }
        if (parent.isFile()) {
            throw new RuntimeException("目录名已被占用：" + parent.getAbsolutePath());
        }
        // 这一步包含文件的创建
        item.write(uploadFile);
        ResultEntity resp = ResultEntity.ok();
        resp.put("filePath", uploadFile.getAbsolutePath());
        resp.put("fileUrl", "/file" + fileName);
        logger.info("上传文件：" + uploadFile.getAbsolutePath());
        pw.print(resp);
    }

    private static String newFileName(String fileName) {
        int index = fileName.lastIndexOf('.');
        String suffix = fileName.substring(index);
        String name = fileName.substring(0, index);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dataStr = sdf.format(date);
        return name + "_" + dataStr + suffix;
    }
}

