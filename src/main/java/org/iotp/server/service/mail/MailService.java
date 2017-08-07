package org.iotp.server.service.mail;

import org.iotp.server.exception.IoTPException;

import com.fasterxml.jackson.databind.JsonNode;

public interface MailService {

    void updateMailConfiguration();

    void sendEmail(String email, String subject, String message) throws IoTPException;
    
    void sendTestMail(JsonNode config, String email) throws IoTPException;
    
    void sendActivationEmail(String activationLink, String email) throws IoTPException;
    
    void sendAccountActivatedEmail(String loginLink, String email) throws IoTPException;
    
    void sendResetPasswordEmail(String passwordResetLink, String email) throws IoTPException;
    
    void sendPasswordWasResetEmail(String loginLink, String email) throws IoTPException;
    
}
