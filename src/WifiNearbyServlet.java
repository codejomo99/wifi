import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/wifiNear")
public class WifiNearbyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userLat = request.getParameter("lat");
        String userLng = request.getParameter("lng");

        if (userLat == null || userLng == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"위치 정보가 필요합니다.\"}");
            return;
        }

        System.out.println(userLat + " " + userLng);

        String apiKey = "6b494b70436a6f6d313330577752636e"; // 실제 API 키로 교체
        StringBuilder result = new StringBuilder();

        try {
            String apiUrl = "http://openapi.seoul.go.kr:8088/" + URLEncoder.encode(apiKey, "UTF-8") +
                    "/json/TbPublicWifiInfo/1/1000";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // JSON 문자열에서 직접 파싱
        String responseJson = result.toString();
        String[] rows = responseJson.split("\"row\":\\[")[1].split("]")[0].split("\\},\\{");
        StringBuilder filteredJson = new StringBuilder();
        filteredJson.append("{\"wifiData\":[");

        double userLatDouble = Double.parseDouble(userLat);
        double userLngDouble = Double.parseDouble(userLng);

        // Set을 사용하여 중복된 Wi-Fi 이름을 저장
        Set<String> seenWifiNames = new HashSet<>();

        for (String row : rows) {
            if (!row.startsWith("{")) row = "{" + row;
            if (!row.endsWith("}")) row = row + "}";

            String wifiName = extractValue(row, "\"X_SWIFI_MAIN_NM\":\"");
            if (seenWifiNames.contains(wifiName)) {
                continue; // 중복된 Wi-Fi 이름은 건너뜁니다
            }

            String latStr = extractValue(row, "\"LAT\":\"");
            String lngStr = extractValue(row, "\"LNT\":\"");

            double wifiLat = Double.parseDouble(latStr);
            double wifiLng = Double.parseDouble(lngStr);
            double distance = calculateDistance(userLatDouble, userLngDouble, wifiLat, wifiLng);

            if (distance <= 10) {
                row = row.substring(0, row.length() - 1) + ",\"distance\":" + distance + "}";
                filteredJson.append(row).append(",");
                seenWifiNames.add(wifiName); // 중복을 피하기 위해 Wi-Fi 이름을 Set에 추가
            }
        }

        if (filteredJson.charAt(filteredJson.length() - 1) == ',') {
            filteredJson.deleteCharAt(filteredJson.length() - 1);
        }
        filteredJson.append("]}");

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(filteredJson);
        out.flush();
    }

    private String extractValue(String json, String key) {
        int startIndex = json.indexOf(key) + key.length();
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
