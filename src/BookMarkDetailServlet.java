import dto.Bookmark;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/bookmarkdetail")
public class BookMarkDetailServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/wifi_service";
    private static final String DB_USER = "test";
    private static final String DB_PASSWORD = "yy223200@@";


    // 드라이버 로드
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // MySQL JDBC 드라이버 로드
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver 로드 실패", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT b.id, b.bookmark_name, w.main_nm, bw.created_at " +
                    "FROM bookmark_wifi bw " +
                    "JOIN bookmark b ON bw.bookmark_id = b.id " +
                    "JOIN wifi_info w ON bw.wifi_info_id = w.id";

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                List<Bookmark> bookmarks = new ArrayList<>();

                while (rs.next()) {
                    Bookmark bookmark = new Bookmark();
                    bookmark.setId(rs.getInt("id"));
                    bookmark.setBookmarkName(rs.getString("bookmark_name"));
                    bookmark.setWifiName(rs.getString("main_nm"));
                    bookmark.setCreatedAt(rs.getTimestamp("created_at"));
                    bookmarks.add(bookmark);
                }

                request.setAttribute("bookmarks", bookmarks);
                System.out.println(bookmarks);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/bookMarkDetail.jsp");
                dispatcher.forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("데이터베이스 오류: " + e.getMessage());
        }
    }
}
