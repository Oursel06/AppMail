package com.example.demo;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HelloController {
    private ChangeListener listener;
    @FXML
    private TreeView listingEmails;
    @FXML
    private Label barre_etat;
    @FXML
    private WebView viewver;
    @FXML Parent root;

    private Button attachButton;
    private Stage primaryStage;

    private File attachedFile;

    // Fonction qui permet d'actualiser la liste des mails
    @FXML
    public void refreshAction() {
        System.out.println("Actualisation...");
        listingEmails.getSelectionModel().selectedItemProperty().removeListener(listener);
        initialize();
        barre_etat.setText("Actualisation mail terminée");
    }

    // Fonction qui permet d'afficher une modal (avec "Alert")
        public void afficheDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("A propos");
        alert.setHeaderText("Projet réalisé par Yan Ledoux et Alexandre Oursel");
        alert.setContentText("Ce logiciel est un client email simple permettant de récupérer les mails reçus sur une adresse Gmail. Il est réalisé dans" +
                "le cadre du cours \"Développement desktop \" pour les B3 de Ynov Sophia.");
        alert.showAndWait();
    }

    // Tableau qui récupère les mails du serveur
    private List<Email> listMails = new ArrayList<>();

    public void initialize() {
        // Lecture mails
        listMails = EmailManager.getEmails();
        // Affichage du sujet de chaque mail dans la TreeView
        TreeItem<Object> root = new TreeItem<>("Boîte de réception");
        listMails.forEach(mail ->
                root.getChildren().add(new TreeItem<>(mail))
                );
        listingEmails.setRoot(root);
        root.setExpanded(true);

        // Affichage des infos du mail selectionné dans la webView et actualisation des infos dans la barre d'état
        SelectionModel sm = listingEmails.getSelectionModel();
        listener = (observableValue, oldValue, newValue) -> {
            TreeItem<Email> selectedItem = (TreeItem<Email>) newValue;
            if(selectedItem.getValue() instanceof Email) {
                Email selectedEmail = (Email) selectedItem.getValue();
                // Affichage infos mail dans la webview et barre d'action
                WebEngine webEngine = viewver.getEngine();
                barre_etat.setText("Mail selectionné : " + selectedEmail.getSubject());
                String webContent = "Expediteur : " + selectedEmail.getSender() + "<br><br>" +
                        selectedEmail.getBody();
                webEngine.loadContent(webContent);
            }
        };
        sm.selectedItemProperty().addListener(listener);
    }

    private void attachFile() {
        FileChooser fileChooser = new FileChooser();
        attachedFile = fileChooser.showOpenDialog(primaryStage);
        if (attachedFile != null) {
            attachButton.setText("Pièce jointe : " + attachedFile.getName());
        }
    }

    // Fonction qui permet de créer une modal avec 2 inputs texte, 1 textarea et 1 bouton
    //
    public void showModalMail() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Envoyer un message");
        modal.setResizable(false);

        // Création des views
        Label objectLabel = new Label("Object :");
        TextField objectField = new TextField();
        objectField.setPromptText("Object");
        Label destinataireLabel = new Label("Destinataire :");
        TextField destinataireField = new TextField();
        destinataireField.setPromptText("Destinataire");
        attachButton = new Button("Ajouter pièce jointe");
        attachButton.setOnAction(event -> attachFile());
        Label messageLabel = new Label("Votre message :");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Votre message...");

        // Création du bouton envoyer qui vérifie si le champ destinataire n'est pas vide pour envoyer le mail
        Button sendButton = new Button("Envoyer");
        sendButton.setOnAction((ActionEvent e) -> {
            String object = objectField.getText();
            String message = messageArea.getText();
            String destinataire = destinataireField.getText();
            final String fromEmail = "ourseljava@gmail.com";
            final String password = "gcwblgtezvnecpnb";
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

            // On tente d'envoyer le mail
        Session session = Session.getInstance(props, auth);
        try {
            if(destinataire.length() > 0){
                Multipart multipart = new MimeMultipart();
                // si l'utilisateur choisi une image en PJ on l'ajoute
                if (attachedFile != null) {
                    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                    attachmentBodyPart.attachFile(attachedFile);
                    multipart.addBodyPart(attachmentBodyPart);
                    EmailManager.sendEmail(session, destinataire,object, multipart, message);
                }
                EmailManager.sendEmailWithoutAttachment(session, destinataire,object, message);
                System.out.println("Mail envoyé ! \n Détails : Destinataire => " + destinataire + " object => " + object + " message => " + message );
                modal.close();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Veuillez choisir un destinataire");
                alert.showAndWait();
            }
        }
        catch (Exception ex) {
            System.out.println("ERREUR : ");
            System.out.println(ex.getMessage());
        }
        });

        // Placement des views
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.addRow(0, destinataireLabel, destinataireField);
        gridPane.addRow(1, objectLabel, objectField);
        gridPane.addRow(2, attachButton);
        gridPane.addRow(3, messageLabel, messageArea);
        HBox hbox = new HBox(sendButton);
        hbox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(gridPane, hbox);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        // Ajout de la scene + affichage
        modal.setScene(new Scene(vbox));
        modal.showAndWait();
    }
}