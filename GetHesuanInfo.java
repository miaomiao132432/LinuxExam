import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;

@WebServlet("/get_hesuan")

public class GetHesuanInfo extends HttpServlet {

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

        String number = req.getParameter("number");
        if (number == null) {
            throw new ServletException("please input number");
        }

        PrintWriter out = resp.getWriter();
        String info = jedis.get(number);

        if (info == null) {
            info = getHesuan(number).toString();
            jedis.set(number, info);
            out.printf("%s", info);

        } else {
            out.printf("%s", info);
        }
        out.close();
    }

    public HesuanInfo getHesuan(String number) throws ServletException {
        HesuanInfo info = new HesuanInfo();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();

            String sql = String.format("SELECT * FROM t_hesuan WHERE number=%s", number);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                do {
                    info.id = rs.getInt("id");
                    info.name = rs.getString([填空]);
                    info.number = rs.getString("number");
                    info.result = rs.getString([填空]);
                } while (rs.next());
            } else {
                throw new ServletException("hesuan info not exist");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            try {
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return info;
    }
}
