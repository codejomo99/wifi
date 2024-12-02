<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>서울시 공공와이파이 서비스</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }


        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            font-size: 14px; /* 테이블 폰트 크기 설정 */
        }

        table, th, td {
            border: 1px solid black;
        }

        th, td {
            padding: 8px 12px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }

        td {
            word-wrap: break-word;
        }

        #wifiTableContainer {
            max-width: 1600px;
            margin: 0 auto;
        }
    </style>
</head>
<body>
<h1>서울시 공공와이파이 위치 정보 보기</h1>

<!-- 메뉴 항목 추가 -->
<div class="menu">
    <a href="/">홈</a>
    <a href="/history">위치 히스토리 목록</a>
    <a href="/wifi">Open API 와이파이 정보 가져오기</a>
    <a href="/favorites">즐겨찾기 보기</a>
    <a href="/group">즐겨찾기 그룹 관리</a>
</div>


<!-- 내 위치 및 근처 Wi-Fi 정보 보기 -->
<div class="location-input">
    <h3>내 위치 정보</h3>
    <label for="lat">LAT : </label>
    <input type="text" id="lat" name="lat" readonly>
    <label for="lng">LNT : </label>
    <input type="text" id="lng" name="lng" readonly>

    <button type="button" onclick="getLocation()">내 위치 가져오기</button>
    <button type="button" onclick="getNearbyWifi()">근처 와이파이 정보 보기</button>

    <!-- 와이파이 테이블 -->
    <div id="wifiTableContainer">
        <!-- 테이블 헤더만 미리 표시 -->
        <table>
            <thead>
            <tr>
                <th>거리 (km)</th>
                <th>관리번호</th>
                <th>자치구</th>
                <th>와이파이명</th>
                <th>도로명주소</th>
                <th>상세주소</th>
                <th>설치위치</th>
                <th>설치유형</th>
                <th>설치기관</th>
                <th>서비스구분</th>
                <th>망종류</th>
                <th>설치년도</th>
                <th>실내외구분</th>
                <th>LAT</th>
                <th>LNT</th>
                <th>작업일자</th>
            </tr>
            </thead>
            <tbody>
            <!-- AJAX로 데이터가 들어갈 부분 -->
            </tbody>
        </table>
    </div>

    <script>
        // 내 위치를 가져오는 함수 (테스트 좌표로 설정)
        function getLocation() {
            // 테스트용 서울 시청 좌표 (37.5665, 126.9780)
            var testLat = 37.5665;
            var testLng = 126.9780;

            // 위도, 경도 값을 입력 필드에 표시
            document.getElementById('lat').value = testLat;
            document.getElementById('lng').value = testLng;

            console.log(`Location set: lat = ${testLat}, lng = ${testLng}`);
        }

        // 근처 와이파이 정보를 가져오는 함수
        function getNearbyWifi() {
            var lat = document.getElementById('lat').value;
            var lng = document.getElementById('lng').value;

            // lat, lng 값이 비어 있으면 오류 처리
            if (!lat || !lng) {
                alert("위치 정보를 먼저 가져와 주세요.");
                return;
            }
            console.log(lat + " " + lng);
            console.log(`lat: ${lat}, lng: ${lng}`);  // 확인용 로그

            // AJAX 요청을 보내서 서버에서 근처 와이파이 정보 받기
            var xhr = new XMLHttpRequest();
            xhr.open("GET", "/wifiNear?lat=" + lat + "&lng=" + lng, true);
            xhr.setRequestHeader("Content-Type", "application/json");

            xhr.onload = function () {
                if (xhr.status === 200) {
                    var response = JSON.parse(xhr.responseText); // 응답 데이터 파싱
                    var wifiData = response.wifiData; // 서버 응답에서 wifiData 가져오기
                    displayWifiData(wifiData); // 데이터를 테이블로 표시
                } else {
                    alert("와이파이 정보를 가져오는 데 실패했습니다.");
                }
            };

            xhr.send();
        }

        // 와이파이 데이터를 테이블 형식으로 표시
        function displayWifiData(wifiData) {
            var tableHtml = "";

            wifiData.forEach(function (wifi) {
                tableHtml += "<tr>" +
                    "<td>" + wifi.distance.toFixed(2) + " km</td>" +
                    "<td>" + wifi.X_SWIFI_MGR_NO + "</td>" +
                    "<td>" + wifi.X_SWIFI_WRDOFC + "</td>" +
                    "<td><a href='/wifiDetails?wifiName=" + wifi.X_SWIFI_MAIN_NM + "'>" + wifi.X_SWIFI_MAIN_NM + "</a></td>" +
                    "<td>" + wifi.X_SWIFI_ADRES1 + "</td>" +
                    "<td>" + wifi.X_SWIFI_ADRES2 + "</td>" +
                    "<td>" + wifi.X_SWIFI_INSTL_FLOOR + "</td>" +
                    "<td>" + wifi.X_SWIFI_INSTL_TY + "</td>" +
                    "<td>" + wifi.X_SWIFI_INSTL_MBY + "</td>" +
                    "<td>" + wifi.X_SWIFI_SVC_SE + "</td>" +
                    "<td>" + wifi.X_SWIFI_CMCWR + "</td>" +
                    "<td>" + wifi.X_SWIFI_CNSTC_YEAR + "</td>" +
                    "<td>" + wifi.X_SWIFI_INOUT_DOOR + "</td>" +
                    "<td>" + wifi.LAT + "</td>" +
                    "<td>" + wifi.LNT + "</td>" +
                    "<td>" + wifi.WORK_DTTM + "</td>" +
                    "</tr>";
            });

            // 테이블 본문에 추가
            document.querySelector("#wifiTableContainer tbody").innerHTML = tableHtml;
        }
    </script>
</body>
</html>
