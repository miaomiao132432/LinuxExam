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

@WebServlet("/add_hesuan")

public class AddHesuanInfo extends HttpServlet {

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
        info.name = req.getParameter("name");
        info.number = req.getParameter("number");
        info.result = req.getParameter("result");

        if (info.number == null || info.name == null || info.result == null) {
            throw new ServletException("please input all parameters");
        }

        addHesuan(info);
        jedis.del(info.number);

        PrintWriter out = resp.getWriter();
        out.printf("[add]ok");
        out.close();

    }

    public void addHesuan(HesuanInfo info) throws ServletException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO t_hesuan(number, name, result) VALUES(?, ?, ?)");
            stmt.setString(1, info.number);
            [仿照上一句, 将info中的name、result参数填入sql中]

            int row = stmt.executeUpdate();
            if (row == 0) {
                throw new ServletException("insert error.");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
