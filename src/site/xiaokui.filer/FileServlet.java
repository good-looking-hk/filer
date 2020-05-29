package site.xiaokui.filer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 文件访问
 * @author HK
 * @date 2019-10-30 17:56
 */
@WebServlet(urlPatterns="/file/*")
public class FileServlet extends HttpServlet {

    private static final String SLASH = File.separator;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = reqStr((req.getRequestURL()));
        if (path == null || "".equals(path)) {
            return;
        }
        path = WebConfig.fileUploadDir + SLASH + path;
        File file = new File(path);
        if (!file.exists()) {
            res.setContentType("text/plain;charset=utf-8");
            res.getWriter().print("404 file not found");
        }
        // 只浏览图片或者只下载
        boolean onlyView = req.getParameter("download") == null;
        if (onlyView && CommonUtil.isImage(path)) {
            res.setContentType("image/jpeg");
        } else {
            res.setContentType("application/octet-stream");
            res.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(file.getName(), "UTF-8"));
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] temp = new byte[1024];
        while (bis.read(temp) != -1) {
            res.getOutputStream().write(temp);
        }
        res.flushBuffer();
    }

    private static String reqStr (StringBuffer fullUrl) {
        int index = fullUrl.indexOf("/file/");
        if (index < 0) {
            return null;
        }
        return fullUrl.substring(index + 6);
    }
}


