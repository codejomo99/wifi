<%@ page import="dto.Bookmark" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>즐겨찾기 목록</title>
</head>

<style>

    body {
        font-family: Arial, sans-serif;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
        font-size: 14px;
    }

    table, th, td {
        border: 1px solid black;
    }

    th, td {
        padding: 8px 12px;
        text-align: left;
        word-wrap: break-word; /* 긴 단어는 줄바꿈 */
        white-space: normal; /* 텍스트가 자동으로 줄 바꿈되게 */
        max-width: 200px; /* 모든 td의 최대 너비 설정 */
        overflow: hidden; /* 넘치는 내용은 숨김 처리 */
        text-overflow: ellipsis; /* 넘치면 ... 으로 표시 */
    }

    th {
        background-color: #f2f2f2;
        font-weight: bold;
    }

</style>

<body>

<h1>즐겨찾기 목록</h1>

<!-- 메뉴 항목 추가 -->
<div class="menu">
    <a href="/">홈</a>
    <a href="/history">위치 히스토리 목록</a>
    <a href="/wifi">Open API 와이파이 정보 가져오기</a>
    <a href="/bookmarkdetail">즐겨찾기 보기</a>
    <a href="/bookMark.jsp">즐겨찾기 그룹 관리</a>
</div>

<!-- 북마크 테이블 -->
<table border="1">
    <thead>
    <tr>
        <th>ID</th>
        <th>북마크 이름</th>
        <th>와이파이명</th>
        <th>등록일자</th>
        <th>비고</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Bookmark> bookmarks = (List<Bookmark>) request.getAttribute("bookmarks");
        if (bookmarks != null) {
            for (Bookmark bookmark : bookmarks) {
    %>
    <tr>
        <td><%= bookmark.getId() %></td>
        <td><%= bookmark.getBookmarkName() %></td>
        <td><%= bookmark.getWifiName() %></td>
        <td><%= bookmark.getCreatedAt() != null ? bookmark.getCreatedAt().toString() : "" %></td>
        <td>비고 내용</td>
    </tr>
    <%
            }
        }
    %>
    </tbody>
</table>

</body>
</html>
