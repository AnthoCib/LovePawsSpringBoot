package com.lovepaws.app.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	@Autowired
	private final JavaMailSender mailSender;

	@Async
	public void enviarCorreo(String para, String asunto, String contenido) {

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(para);
			helper.setSubject(asunto);
			helper.setText(contenido, true); // true = HTML
			helper.setFrom("adminlovepaws@gmail.com"); 

			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
