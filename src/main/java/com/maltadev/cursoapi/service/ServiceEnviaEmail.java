package com.maltadev.cursoapi.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class ServiceEnviaEmail {

	private String userName = "marwamdev.testes@gmail.com";
	private String senha = "KKE9ODHY";
	
	
	public void enviarEmail (String assunto, String emailDestino, String mensagem) throws Exception {
		
		//Vamos dizer para o java quais são as configurações de e-mail
		Properties properties = new Properties();
		properties.put("mail.smtp.ssl.trust", "*");
		properties.put("mail.smtp.auth", "true"); //Autoriza email a passar usuário e senha
		properties.put("mail.smtp.starttls", "true"); //Autenticação
		properties.put("mail.smtp.host", "smtp.gmail.com"); //Servidor do google
		properties.put("mail.smtp.port", "465"); //Porta do servidor
		properties.put("mail.smtp.socketFactory.port", "465"); //Especifica porta socket
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //Class de conexão com o socket
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, senha);
			};
		});
		
		
		Address[] toUser = InternetAddress.parse(emailDestino);
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(userName)); //Quem está enviando (no caso o dono do projeto)
		message.setRecipients(Message.RecipientType.TO, toUser); //Enviar para quem vai o email - Quem irá receber
		message.setSubject(assunto); //Assunto do email
		message.setText(mensagem);
		
		
		Transport.send(message);		
		
		
	}
	
}
