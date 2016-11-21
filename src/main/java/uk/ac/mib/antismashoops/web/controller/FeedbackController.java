package uk.ac.mib.antismashoops.web.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.mib.antismashoops.core.services.FeedbackService;

@Controller
public class FeedbackController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

	@Autowired
	FeedbackService fs;

	@Value("${spring.mail.username}")
	private String oopsEmail;

	@RequestMapping("/feedback")
	public String loadFeedback() {
		return "feedback";
	}

	@RequestMapping(value = "/sendFeedback", method = RequestMethod.POST)
	public String sendFeedback(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "subject", required = true) String subject,
			@RequestParam(value = "body", required = true) String body, ModelMap model) throws MessagingException {

		String userBody = "Dear " + name
				+ ",<br /><br />Thank you for the feedback provided, below are your comments:<br /><b><p>" + body
				+ "</p></b>We will be in touch soon!<br /><br />Best regards,<br />The antiSMASH - OOPS team";
		String userSubject = "antiSMASH - OOPS Feedback Received: " + subject;

		String feedbackBody = "Feedback sent by: " + name + " with email: " + email + "<br /><br />" + body;

		fs.sendFeedback(email, userSubject, userBody);
		fs.sendFeedback(oopsEmail, subject, feedbackBody);

		model.addAttribute("result", "ok");
		return "feedback";
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception) {
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
		logger.error("Exception thrown: " + exception.getClass());
		logger.error("Exception message: " + exception.getMessage());
		exception.printStackTrace();
		return "error";
	}
}
