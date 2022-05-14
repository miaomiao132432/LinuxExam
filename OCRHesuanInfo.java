import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ocr_hesuan")
public class OCRHesuanInfo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        String file = req.getParameter("file");
        String number = req.getParameter("number");
        if (file == null || number == null) {
            throw new ServletException("please input all parameters");
        }

        OCREngine engine = new OCREngine();
        HesuanInfo info = new HesuanInfo();
        try {
            info = engine.run(String.format("[填写你的图片目录的绝对路径]/%s", file));
        } catch (Exception e) {
            throw new ServletException(e);
        }

        String url = [请填入跳转到/add_hesuan的url, 注意utf-8编码]

        resp.sendRedirect(url);
    }
}
