<%--
  Created by IntelliJ IDEA.
  User: codejomo99
  Date: 12/2/24
  Time: 4:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>
<%
    String url = "jdbc:mysql://localhost:3306/wifi_service?useUnicode=true&characterEncoding=UTF-8";
    String user = "test";
    String password = "yy223200@@";

    Connection conn = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(url, user, password);
        out.println("DB 연결 성공!");
    } catch (Exception e) {
        out.println("DB 연결 실패: " + e.getMessage());
    } finally {
        if (conn != null) conn.close();
    }
%>
