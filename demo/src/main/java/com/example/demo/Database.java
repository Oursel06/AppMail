package com.example.demo;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static final String HOST = "localhost";
    public static final String PORT = "3306";
    public static final String DATABASE_NAME = "appmail";
    public static final String USER = "root";
    public static final String PASSWORD = "";
    public static final String DATABASE_URL = "jdbc:mysql://"+HOST+":"+PORT+"/"+DATABASE_NAME;

    // Fonction permettant de se connecter à une base de données MYSQL
    public Connection dbConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        return conn;
    }

    // Fonction qui insert un mail
    public void insertMail(String subject, String sender, String date, String body) throws SQLException {
        String query = "INSERT INTO mail(subject, sender, date, body) VALUES (?, ?, ?, ?)";
        PreparedStatement st = dbConnection().prepareStatement(query);
        st.setString(1, subject);
        st.setString(2, sender);
        st.setString(3, date);
        st.setString(4, body);

        int nb = st.executeUpdate();
        System.out.println(nb + " ligne(s) inserée(s)");
        st.close();
    }

    // Fonction qui récupère tous les mails de la base de données
    public List<String> displayMail() throws SQLException {
        Statement st = dbConnection().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM mail");
        List<String> dateEmail = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String subject = rs.getString("subject");
            String sender = rs.getString("sender");
            String date = rs.getString("date");
            String laDate = rs.getString("date");
            String body = rs.getString("body");
//            System.out.println("Mail N° " + id + " : \nsubject : " + subject + "\nsender : " + sender + "\ndate : " + date +"\nbody : " + body +"\n");
            dateEmail.add(laDate);
        }
        st.close();
        return dateEmail;
    }

    // Fonction qui hash un mot de passe passé en paramètre
    public String get_SHA_512_SecurePassword(String passwordToHash, String salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
