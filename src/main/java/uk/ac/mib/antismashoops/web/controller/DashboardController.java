package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController
{
	@Value("${app.files.uploadpath}")
	private String uploadPath;

	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@RequestMapping("/dashboard")
	public String showResults(@RequestParam("fileName") String fileName, ModelMap model) throws IOException
	{
		logger.info("Loading Dashboard");
		model.addAttribute("fileName", fileName);
		return "dashboard";
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception)
	{
		exception.printStackTrace();
		req.setAttribute("message", exception.getMessage());
		return "error";
	}
}
