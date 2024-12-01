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

@WebServlet("/wifi")
public class WifiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String apiKey = "6b494b70436a6f6d313330577752636e";  // 실제 API 키로 교체하세요
        StringBuilder result = new StringBuilder();

        System.out.println("WifiServlet 요청 들어옴!");

        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
            urlBuilder.append("/").append(URLEncoder.encode(apiKey, "UTF-8"));
            urlBuilder.append("/").append(URLEncoder.encode("json", "UTF-8"));
            urlBuilder.append("/").append(URLEncoder.encode("TbPublicWifiInfo", "UTF-8"));
            urlBuilder.append("/").append(URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("/").append(URLEncoder.encode("5", "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }



        // JSON 결과를 JSP로 전달
        request.setAttribute("wifiData", result.toString());
        request.getRequestDispatcher("/wifiResult.jsp").forward(request, response);
    }


}
