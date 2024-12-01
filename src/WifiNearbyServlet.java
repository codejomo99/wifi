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

@WebServlet("/wifiNear")
public class WifiNearbyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String apiKey = "6b494b70436a6f6d313330577752636e";  // 실제 API 키로 교체하세요
        StringBuilder result = new StringBuilder();

        System.out.println("실행되었습니다.");

        // 사용자 위치 정보 받기
        String latParam = request.getParameter("lat");
        String lngParam = request.getParameter("lng");

        // lat, lng 값이 비어 있거나 숫자가 아니면 에러 처리
        if (latParam == null || lngParam == null || latParam.isEmpty() || lngParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "위치 정보가 부족합니다.");
            return;
        }

        double userLat = 0;
        double userLng = 0;
        try {
            userLat = Double.parseDouble(latParam);  // lat를 double로 변환
            userLng = Double.parseDouble(lngParam);  // lng를 double로 변환
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 위치 정보 형식입니다.");
            return;
        }

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

        // 받아온 Wi-Fi 데이터 JSON 파싱 (순수 문자열 처리)
        String wifiJsonData = result.toString();
        String wifiDataArray = extractJsonArray(wifiJsonData);

        // 근처 Wi-Fi 필터링
        StringBuilder nearbyWifiArray = new StringBuilder();
        String[] wifiEntries = wifiDataArray.split("},");  // 각 Wi-Fi 항목 분리

        for (String wifiEntry : wifiEntries) {
            double wifiLat = extractDoubleValue(wifiEntry, "LAT");
            double wifiLng = extractDoubleValue(wifiEntry, "LNT");

            // 거리 계산 (Haversine 공식)
            double distance = calculateDistance(userLat, userLng, wifiLat, wifiLng);
            if (distance <= 1.0) {  // 1km 이내
                if (nearbyWifiArray.length() > 0) {
                    nearbyWifiArray.append(",");
                }
                nearbyWifiArray.append("{").append(wifiEntry).append("}");
            }
        }

        // 필터링된 Wi-Fi 데이터 응답으로 반환
        // 필터링된 Wi-Fi 데이터 응답으로 반환
        response.setContentType("application/json");
        response.getWriter().write("{\"wifiData\": [" + nearbyWifiArray.toString() + "]}");
    }

    // Haversine formula로 두 지점 간의 거리를 계산하는 메소드
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반경 (단위: km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // 단위: km
        return distance;
    }

    // JSON 데이터에서 배열 부분만 추출하는 메소드
    private String extractJsonArray(String jsonData) {
        int start = jsonData.indexOf("[");
        int end = jsonData.lastIndexOf("]");
        return jsonData.substring(start + 1, end);  // "["와 "]" 제외한 부분
    }

    // JSON 항목에서 특정 값을 추출하는 메소드 (숫자 값)
    private double extractDoubleValue(String jsonEntry, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = jsonEntry.indexOf(searchKey);
        if (startIndex != -1) {
            int valueStart = startIndex + searchKey.length();
            int valueEnd = jsonEntry.indexOf(",", valueStart);
            if (valueEnd == -1) valueEnd = jsonEntry.indexOf("}", valueStart);
            String value = jsonEntry.substring(valueStart, valueEnd).trim();
            return Double.parseDouble(value);
        }
        return 0.0;
    }


}
