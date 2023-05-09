package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 620, 340);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Creation mail
        final String fromEmail = "ourseljava@gmail.com";
        final String password = "gcwblgtezvnecpnb";
        final String toEmail = "ourseljava@gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
//        Session session = Session.getInstance(props, auth);
//        EmailManager.sendEmail(session, toEmail,"deuxieme test", "deuxieme body !");

        // modification
//        try {
//            maDB.updateUser(1, "Alexandre", "alex@gmail.com");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        // Supprimer
//        try {
//            maDB.deleteUser(3);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        launch();
    }
}