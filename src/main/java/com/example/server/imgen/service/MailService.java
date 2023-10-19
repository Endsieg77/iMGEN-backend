package com.example.server.imgen.service;

import javax.mail.MessagingException;

public interface MailService  {

    public void sendSimpleMail(String to, String subject, String content);

    // public void sendHtmlMail(String to, String subject, String content) throws MessagingException;
    
}