package site.xiaokui.filer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 长链接转换短链接
 * @author HK
 * @date 2020-05-29 19:32
 */
@WebServlet(urlPatterns="/shortUrl/*")
public class ShortUrlServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        doGet(req, res);
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

    }
}
