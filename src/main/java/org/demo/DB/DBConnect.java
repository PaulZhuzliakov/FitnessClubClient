//package org.demo.DB;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import org.demo.model.ClubClient;
//
//import javax.swing.*;
//import java.sql.*;
//
//public class DBConnect {
//    final String url = "jdbc:postgresql://localhost:5432/fitness_club";
//    final String user = "postgres";
//    final String pass = "postgres";
//    Connection conn = null;
//
//    public static Connection connectDB() {
//        try {
//            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/fitness_club",
//                    "postgres", "postgres");
////            JOptionPane.showMessageDialog(null, "Соединение установленно");
//            return conn;
//        } catch (SQLException e) {
////            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, e);
//            return null;
//        }
//    }
//
//    public static ObservableList<ClubClient> getDataUsers(){
//        Connection conn = connectDB();
//        ObservableList<ClubClient> list = FXCollections.observableArrayList();
//        String sql = "SELECT * FROM clients";
//        try {
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()){
//                list.add(new ClubClient(Integer.parseInt(String.valueOf(rs.getInt("club_card_number"))),
//                        rs.getString("last_name"),
//                        rs.getString("first_name"),
//                        rs.getString("middle_name")));
//            }
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//}
