package site.xiaokui.filer;

import cn.hutool.core.codec.Base64;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 长链接转换短链接
 * @author HK
 * @date 2020-05-29 19:32
 */
@WebServlet(urlPatterns={"/s/*"})
public class ShortUrlServlet extends HttpServlet {

    private static Logger logger = LogManager.getLogger(ShortUrlServlet.class);

    private static final String SHORT_URL_DIR = WebConfig.shortUrlDir;

    private static final String ROOT_URL = WebConfig.rootUrl;

    private static final String HTML_SUFFIX = ".html";

    private static final int MAX_GENE_FAIL_NUM = 3;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=utf-8");
        String filename = reqStr((req.getRequestURL()));
        File file = new File(SHORT_URL_DIR + filename + HTML_SUFFIX);
        if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String str = bufferedReader.readLine();
            bufferedReader.close();
            resp.getWriter().write(str);
        } else {
            resp.getWriter().write("<h3>404, sorry this page is missing<h3>");
        }
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        String longUrl = req.getParameter("longUrl");
        String shortUrl = null;

        int failNum = 1;
        while (failNum < MAX_GENE_FAIL_NUM && ((shortUrl = geneProxyHtmlFile(longUrl)) == null)) {
            failNum++;
        }
        if (failNum >= MAX_GENE_FAIL_NUM) {
            logger.error("生成短链接html代理文件失败，请检查！");
            resp.getWriter().write("{\"msg\":\"出现未知错误，请联系管理员QQ467914950\"}");
            return;
        }

        ResultEntity result = ResultEntity.ok().put("shortUrl", ROOT_URL + shortUrl).put("longUrl", longUrl);
        resp.getWriter().write(result.toString());
        resp.flushBuffer();
    }

    private String geneProxyHtmlFile(String longUrl) throws IOException {
        long now = System.currentTimeMillis();
        String shortUrl = Integer.toHexString((int)now);
        File file = new File(SHORT_URL_DIR + File.separator + shortUrl + HTML_SUFFIX);
        if (file.exists()) {
            return null;
        } else {
            boolean success = file.createNewFile();
            if (!success) {
                return null;
            }
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("<head><meta http-equiv=\"refresh\" content=\"0;url=" + longUrl + "\"></head>");
        fileWriter.flush();
        fileWriter.close();
        return shortUrl;
    }

    private static String reqStr (StringBuffer fullUrl) {
        int index = fullUrl.indexOf("/s/");
        if (index < 0) {
            return null;
        }
        return fullUrl.substring(index + 3);
    }

    public static void main(String[] args)  {
        long a = System.nanoTime();
        System.out.println(a);
        long b = System.currentTimeMillis();
        System.out.println(b);
        System.out.println(Integer.toHexString((int)a));
        System.out.println(Long.toHexString(a));
        System.out.println(Integer.toHexString((int)b));
        System.out.println(Long.toHexString(b));

        String aa = Base64.encode(Long.toHexString(b));
        System.out.println(aa);
    }
}
