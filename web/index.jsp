<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>서울시 공공와이파이 서비스</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        .menu {
            margin: 20px 0;
        }

        .menu a {
            margin-right: 10px;
            text-decoration: none;
            color: #007bff;
        }

        .location-input {
            margin-top: 20px;
        }

        .button-container {
            display: flex;
            justify-content: space-between;
            max-width: 500px;
        }

        .button-container button {
            padding: 10px 20px;
            margin-right: 10px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        table, th, td {
            border: 1px solid black;
        }

        th, td {
            padding: 10px;
            text-align: left;
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

<!-- 공공 와이파이 위치 정보 버튼 -->
<form action="/wifi" method="GET">
    <button type="submit">공공와이파이 위치 보기</button>
</form>

<!-- 내 위치 및 근처 Wi-Fi 정보 보기 -->
<div class="location-input">
    <h3>내 위치 정보</h3>
    <label for="lat">LAT : </label>
    <input type="text" id="lat" name="lat" readonly>
    <label for="lng">LNT : </label>
    <input type="text" id="lng" name="lng" readonly>

    <!-- 버튼을 가로로 나열 -->
    <div class="button-container">
        <button type="button" onclick="getLocation()">내 위치 가져오기</button>
        <button type="button" onclick="getNearbyWifi()">근처 와이파이 정보 보기</button>
    </div>
</div>

<!-- 와이파이 테이블 -->
<div id="wifiTableContainer"></div>

<script>
    // 내 위치를 가져오는 함수
    function getLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                // 위도, 경도 값을 입력 필드에 표시
                document.getElementById('lat').value = position.coords.latitude;
                document.getElementById('lng').value = position.coords.longitude;

                console.log(`Location set: lat = ${position.coords.latitude}, lng = ${position.coords.longitude}`);
            }, function (error) {
                alert("위치를 가져오는 데 실패했습니다.");
            });
        } else {
            alert("이 브라우저는 위치 정보를 지원하지 않습니다.");
        }
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
        console.log(lat+" "+lng);
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
        var tableHtml = "<table><thead><tr>" +
            "<th>거리 (km)</th>" +
            "<th>관리번호</th>" +
            "<th>자치구</th>" +
            "<th>와이파이명</th>" +
            "<th>도로명주소</th>" +
            "<th>상세주소</th>" +
            "<th>설치위치</th>" +
            "<th>설치유형</th>" +
            "<th>설치기관</th>" +
            "<th>서비스구분</th>" +
            "<th>망종류</th>" +
            "<th>설치년도</th>" +
            "<th>실내외구분</th>" +
            "<th>접속환경</th>" +
            "<th>LAT</th>" +
            "<th>LNT</th>" +
            "<th>작업일자</th>" +
            "</tr></thead><tbody>";

        wifiData.forEach(function (wifi) {
            tableHtml += "<tr>" +
                "<td>" + wifi.distance + "</td>" +
                "<td>" + wifi.mgrNo + "</td>" +
                "<td>" + wifi.gu + "</td>" +
                "<td>" + wifi.wifiName + "</td>" +
                "<td>" + wifi.address + "</td>" +
                "<td>" + wifi.detailAddress + "</td>" +
                "<td>" + wifi.installationLocation + "</td>" +
                "<td>" + wifi.installationType + "</td>" +
                "<td>" + wifi.installationAgency + "</td>" +
                "<td>" + wifi.serviceType + "</td>" +
                "<td>" + wifi.networkType + "</td>" +
                "<td>" + wifi.installationYear + "</td>" +
                "<td>" + wifi.indoorOutdoor + "</td>" +
                "<td>" + wifi.connectionEnvironment + "</td>" +
                "<td>" + wifi.lat + "</td>" +
                "<td>" + wifi.lng + "</td>" +
                "<td>" + wifi.workDate + "</td>" +
                "</tr>";
        });

        tableHtml += "</tbody></table>";

        // 테이블을 화면에 추가
        document.getElementById('wifiTableContainer').innerHTML = tableHtml;
    }
</script>
</body>
</html>
