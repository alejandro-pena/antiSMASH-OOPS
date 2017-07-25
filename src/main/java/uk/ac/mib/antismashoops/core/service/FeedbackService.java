package uk.ac.mib.antismashoops.core.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

	private JavaMailSender mailSender;

	@Autowired
	public FeedbackService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendFeedback(String to, String subject, String body) throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body, true);

		mailSender.send(message);

	}
}
