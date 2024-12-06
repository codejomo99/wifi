<%--
  Created by IntelliJ IDEA.
  User: codejomo99
  Date: 12/6/24
  Time: 3:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.SQLException" %>
<%
    // Database credentials
    String jdbcUrl = "jdbc:mysql://localhost:3306/wifi_service"; // Replace with your database URL
    String username = "test"; // Replace with your database username
    String password = "yy223200@@"; // Replace with your database password

    Connection connection = null;
    try {
        // Load JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver"); // Use the appropriate driver for your database

        // Attempt to connect to the database
        connection = DriverManager.getConnection(jdbcUrl, username, password);

        // Check if the connection is successful
        if (connection != null) {
            out.println("Database connection successful!");
        } else {
            out.println("Failed to connect to the database.");
        }
    } catch (ClassNotFoundException e) {
        out.println("JDBC Driver not found: " + e.getMessage());
    } catch (SQLException e) {
        out.println("Database connection error: " + e.getMessage());
    } finally {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                out.println("Error closing the connection: " + e.getMessage());
            }
        }
    }
%>
