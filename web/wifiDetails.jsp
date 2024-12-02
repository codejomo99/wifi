<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>


    <h1>서울시 공공와이파이 위치 정보 보기</h1>

    <!-- 메뉴 항목 추가 -->
    <div class="menu">
        <a href="/">홈</a>
        <a href="/history">위치 히스토리 목록</a>
        <a href="/wifi">Open API 와이파이 정보 가져오기</a>
        <a href="/favorites">즐겨찾기 보기</a>
        <a href="/group">즐겨찾기 그룹 관리</a>
    </div>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        .details-container {
            width: 80%;
            margin: 20px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 8px;
            background-color: #f9f9f9;
        }

        h2 {
            text-align: center;
        }

        .detail-item {
            margin-bottom: 10px;
        }

        .detail-item span {
            font-weight: bold;
        }

        .back-btn {
            margin-top: 20px;
            text-align: center;
        }

        .back-btn a {
            padding: 10px 20px;
            background-color: #007bff;
            color: #fff;
            text-decoration: none;
            border-radius: 4px;
        }

        .back-btn a:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
<div class="details-container">
    <h2>와이파이 상세 정보</h2>

    <c:if test="${not empty wifiDetails}">
        <div class="detail-item"><span>와이파이명:</span> ${wifiDetails.wifiName}</div>
        <div class="detail-item"><span>관리번호:</span> ${wifiDetails.mgrNo}</div>
        <div class="detail-item"><span>주소:</span> ${wifiDetails.address}</div>
        <div class="detail-item"><span>설치 유형:</span> ${wifiDetails.installationType}</div>
        <div class="detail-item"><span>설치 연도:</span> ${wifiDetails.installationYear}</div>
        <div class="detail-item"><span>설치 기관:</span> ${wifiDetails.installationAgency}</div>
        <div class="detail-item"><span>위도:</span> ${wifiDetails.latitude}</div>
        <div class="detail-item"><span>경도:</span> ${wifiDetails.longitude}</div>
        <div class="detail-item"><span>층수:</span> ${wifiDetails.floor}</div>
        <div class="detail-item"><span>서비스 종류:</span> ${wifiDetails.serviceType}</div>
    </c:if>

    <div class="back-btn">
        <a href="javascript:history.back()">뒤로 가기</a>
    </div>
</div>
</body>
</html>
