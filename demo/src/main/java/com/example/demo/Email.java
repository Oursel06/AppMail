package com.example.demo;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;

public class Email {
    private String body ;
    private String sender ;
    private String recipient ;
    private String subject ;
    private Date date ;

    /**************************************************************
     * Constructeur complet, pour l'envoi ou la réception
     * @param sender
     * @param recipient
     * @param subject
     * @param body
     * @param date
     ***************************************************************/
    public Email(String sender, String recipient, String subject, String body, Date date)
    {
        this.body = body ;
        this.date = date ;
        this.sender = sender ;
        this.subject = subject ;
    }

    /**************************************************
     * Constructeur pour la revception d'email
     * @param sender
     * @param subject
     * @param body
     * @param date
     ****************************************************/
    public Email(String sender, String subject, String body, Date date)
    {
        this (sender, null, subject, body, date) ;
    }

    /**************************************************************
     * Construction d'un email à envoyer
     * @param recipient
     * @param subject
     * @param body
     *******************************************************************/
    public Email(String recipient, String subject, String body)
    {
        this (null, recipient, subject, body, new Date()) ;
    }

    /********************************************************
     * Autre constructeur qui se base sur un objet Message
     * @param message
     ********************************************************/
    public Email (Message message)
    {

        try {
            this.recipient = ((Address)message.getRecipients(Message.RecipientType.TO)[0]).toString() ;
            this.sender = ((Address)message.getFrom()[0]).toString() ;
            this.date = message.getSentDate() ;
            this.subject = message.getSubject();
            this.body = ReadEmails.getTextFromMessage(message) ;
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* GETTERS et SETTERS */

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return subject ;
    }
}