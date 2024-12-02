import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/wifi")
public class WifiServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/wifi_service";
    private static final String DB_USER = "test";
    private static final String DB_PASSWORD = "yy223200@@";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String apiKey = "6b494b70436a6f6d313330577752636e";

        StringBuilder result = new StringBuilder();

        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
            urlBuilder.append("/").append(URLEncoder.encode(apiKey, "UTF-8"));
            urlBuilder.append("/").append("json");
            urlBuilder.append("/").append("TbPublicWifiInfo");
            urlBuilder.append("/").append("1");
            urlBuilder.append("/").append("1000");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 299
                            ? conn.getInputStream() : conn.getErrorStream()));

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            conn.disconnect();

            int startIndex = result.indexOf("\"row\":[");
            if (startIndex == -1) {
                response.getWriter().println("데이터가 없습니다.");
                return;
            }
            int endIndex = result.indexOf("]", startIndex);
            String rowArrayString = result.substring(startIndex + 7, endIndex);
            String[] wifiArray = rowArrayString.split("},\\{");

            saveWifiDataToDB(wifiArray, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("서버에서 오류가 발생했습니다: " + e.getMessage());
        }

        response.getWriter().println("데이터 수집 및 저장 완료!");
    }

    private void saveWifiDataToDB(String[] wifiArray, HttpServletResponse response) {


        String insertSql =
                "INSERT INTO wifi_info (wifi_mgr_no, wrdofc, main_nm, adres1, adres2, instl_floor, instl_ty, instl_mby, " +
                        "svc_se, cmcwr, cnstc_year, inout_door, remars3, lat, lnt, work_dttm, distance) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

                conn.createStatement().executeUpdate("DELETE FROM wifi_info");

                for (String wifiData : wifiArray) {
                    pstmt.setString(1, extractField(wifiData, "X_SWIFI_MGR_NO"));
                    pstmt.setString(2, extractField(wifiData, "X_SWIFI_WRDOFC"));
                    pstmt.setString(3, extractField(wifiData, "X_SWIFI_MAIN_NM"));
                    pstmt.setString(4, extractField(wifiData, "X_SWIFI_ADRES1"));
                    pstmt.setString(5, extractField(wifiData, "X_SWIFI_ADRES2"));
                    pstmt.setString(6, extractField(wifiData, "X_SWIFI_INSTL_FLOOR"));
                    pstmt.setString(7, extractField(wifiData, "X_SWIFI_INSTL_TY"));
                    pstmt.setString(8, extractField(wifiData, "X_SWIFI_INSTL_MBY"));
                    pstmt.setString(9, extractField(wifiData, "X_SWIFI_SVC_SE"));
                    pstmt.setString(10, extractField(wifiData, "X_SWIFI_CMCWR"));
                    pstmt.setString(11, extractField(wifiData, "X_SWIFI_CNSTC_YEAR"));
                    pstmt.setString(12, extractField(wifiData, "X_SWIFI_INOUT_DOOR"));
                    pstmt.setString(13, extractField(wifiData, "X_SWIFI_REMARS3"));
                    pstmt.setDouble(14, parseDouble(extractField(wifiData, "LAT")));
                    pstmt.setDouble(15, parseDouble(extractField(wifiData, "LNT")));
                    pstmt.setString(16, extractField(wifiData, "WORK_DTTM"));
                    pstmt.setDouble(17, 0.0);

                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                System.out.println("DB 저장 완료!");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            try {
                response.getWriter().println("MySQL 드라이버 오류: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private String extractField(String data, String fieldName) {
        String key = "\"" + fieldName + "\":\"";
        int startIndex = data.indexOf(key);
        if (startIndex == -1) return "";
        startIndex += key.length();
        int endIndex = data.indexOf("\"", startIndex);
        return endIndex == -1 ? "" : data.substring(startIndex, endIndex).replace("\\", "");
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
