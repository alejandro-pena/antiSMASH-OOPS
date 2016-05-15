package uk.ac.mib.antismashoops.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.mib.antismashoops.core.domain.Person;

@Controller
public class IndexController
{
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping("/")
	public String index()
	{
		return "index";
	}

	@RequestMapping("/person")
	public String person(Model model)
	{
		Person p = new Person();
		p.setFirstName("Alex");
		p.setLastName("Pena");
		p.setAge(25);
		model.addAttribute("person", p);
		return "personView";
	}
}
