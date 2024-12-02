import dto.WifiDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet("/wifiDetails")
public class WifiDetailsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 요청된 wifiName을 URL 파라미터에서 가져옵니다.
        String wifiName = request.getParameter("wifiName");

        // 실제 데이터는 서울시 공공 와이파이 API나 다른 방식으로 가져올 수 있음
        // 여기서는 예시로 static 데이터로 설정합니다.
        WifiDetails wifiDetails = null;

        // 와이파이 상세 정보 샘플 (API 호출하여 데이터를 가져오는 대신 예시 데이터를 사용)
        if (wifiName != null) {
            wifiDetails = new WifiDetails();
            wifiDetails.setWifiName(wifiName);
            wifiDetails.setMgrNo("ARI00001");
            wifiDetails.setAddress("서소문로 51");
            wifiDetails.setInstallationType("7-1-3. 공공 - 시산하기관");
            wifiDetails.setInstallationYear("2024");
            wifiDetails.setInstallationAgency("서울시(AP)");
            wifiDetails.setLatitude("37.561924");
            wifiDetails.setLongitude("126.96675");
            wifiDetails.setFloor("1F");
            wifiDetails.setServiceType("공공WiFi");
        }

        // 데이터를 request 객체에 저장
        request.setAttribute("wifiDetails", wifiDetails);

        // wifiDetails.jsp로 포워딩
        request.getRequestDispatcher("/wifiDetails.jsp").forward(request, response);
    }
}
