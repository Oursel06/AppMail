package com.example.demo;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmailManager {
    // Fonction qui permet d'envoyer un mail
    public static void sendEmail(Session session, String destinataire, String sujet, Multipart multipart, String message) {
        try {
            MimeMessage msg = new MimeMessage(session);
            // set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setSubject(sujet, "UTF-8");
            msg.setText(message, "UTF-8");
            msg.setContent(multipart);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire, false));
            Transport.send(msg);
            System.out.println("sendEmail => OK !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fonction qui permet de récupérer chaque mails du serveur et de l'insérer en base de données s'il n'y est pas déjà
    public static List<Email> getEmails() {
        List<Email> listEmail = new ArrayList<>();
        List<String> dateEmail = new ArrayList<>();
        // Config
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "993");
        properties.put("mail.smtp.starttls.enable", "true");

        // Selection mails en base de données
        Database maDB = new Database();
        try {
            maDB.displayMail();
            dateEmail = maDB.displayMail();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // On essaie d'acéder au serveur Gmail et de se connecter à la boîte mail sur laquelle on veut récupérer les mails
        Session emailSession = Session.getDefaultInstance(properties);
        try {
            Store store = emailSession.getStore("imaps");
            store.connect("imap.gmail.com", "ourseljava@gmail.com", "gcwblgtezvnecpnb");
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            Message[] messages = emailFolder.getMessages();
            System.out.println("Vous avez " + messages.length + " mail(s) : \n");

            // On boucle sur les mails pour récupérer chaque information de chaque mail
            for (int i = 0, n = messages.length; i < n; i++) {
                Boolean notInDatabase = false;
                Message message = messages[i];
                // Ajout d'un mail
                try {
                Email mail = new Email(((InternetAddress) message.getFrom()[0]).getAddress(), null, message.getSubject(), ReadEmails.getTextFromMessage(message), message.getSentDate());
                listEmail.add(mail);
                    System.out.println("Sujet : " + message.getSubject());
                    System.out.println("De : " + ((InternetAddress) message.getFrom()[0]).getAddress());
                    System.out.println("Date : " + message.getSentDate());
                    System.out.println("Text : " + ReadEmails.getTextFromMessage(message));
                    System.out.println("---------------------------------------\n");

                    // Si la BDD est vide on insert TOUS les mails récupérés
                    if(dateEmail.isEmpty()) {
                        maDB.insertMail(mail.getSubject(), mail.getSender(), mail.getDate().toString(), mail.getBody());
                    }

                    // Boucle qui compare la date de chaque mail de la base avec la date du mail récupéré
                    // Si la date est identique => le mail est en base de données => on sort de la condition
                    // Sinon => le mail n'est pas en base, il faut l'insérer
                for (int j = 0, o = dateEmail.size(); j < o; j++) {
                    if (dateEmail.get(j).equals(mail.getDate().toString())) {
                        notInDatabase = false;
                        break;
                    } else {
                        notInDatabase = true;
                    }
                }
                if(notInDatabase) {
                    maDB.insertMail(mail.getSubject(), mail.getSender(), mail.getDate().toString(), mail.getBody());
                }
            }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            emailFolder.close();
            store.close();

        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return listEmail;
    }
}
