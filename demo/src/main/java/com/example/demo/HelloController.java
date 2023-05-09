package com.example.demo;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.List;

public class HelloController {
    private ChangeListener listener;
    @FXML
    private TreeView listingEmails;
    @FXML
    private Label barre_etat;
    @FXML
    private WebView viewver;
    @FXML
    public void refreshAction() {
        System.out.println("Actualisation...");
        listingEmails.getSelectionModel().selectedItemProperty().removeListener(listener);
        initialize();
        barre_etat.setText("Actualisation mail terminée");
    }

    public void afficheDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("A propos");
        alert.setHeaderText("Projet réalisé par Yan Ledoux et Alexandre Oursel");
        alert.setContentText("Ce logiciel est un client email simple permettant de récupérer les mails reçus sur une adresse Gmail. Il est réalisé dans" +
                "le cadre du cours \"Développement desktop \" pour les B3 de Ynov Sophia.");
        alert.showAndWait();
    }

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
}