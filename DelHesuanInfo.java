import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;

@WebServlet("/del_hesuan")
public class DelHesuanInfo extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = [填空];
    static final String USER = [填空];
    static final String PASS = [填空];

    static Connection conn = null;
    static Jedis jedis = null;

    public void init() throws ServletException {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            jedis = new Jedis("127.0.0.1");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void destroy() {
        try {
            conn.close();
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        HesuanInfo info = new HesuanInfo();
        info.number = req.getParameter("number");

        if (info.number == null) {
            throw new ServletException("please input all parameters");
        }

        delHesuan(info);
        jedis.del(info.number);

        PrintWriter out = resp.getWriter();
        out.printf("[del]ok");
        out.close();

    }

    public void delHesuan(HesuanInfo info) throws ServletException {
        [仿照addHesuan, 完成delHesuan]
    }

}
