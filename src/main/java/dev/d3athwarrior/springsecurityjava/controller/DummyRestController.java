/**
 * 
 */
package dev.d3athwarrior.springsecurityjava.controller;

import javax.annotation.security.RolesAllowed;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Anish.Kumar
 *
 */
@RestController
public class DummyRestController {
	
	/*
	 * private SecurityContextHolder ctxHolder = null;
	 * 
	 * @Autowired public DummyRestController(final SecurityContextHolder ctxHolder)
	 * { this.ctxHolder = ctxHolder; }
	 */

	@RequestMapping("/")
	public String hello() {
		if (SecurityContextHolder.getContext().getAuthentication().getName().contains("tomcat"))
			return "Hello tomcatuser";
		else
			return "Hello World";
	}
	
	@RequestMapping("/inaccessible")
	@RolesAllowed({"ROLE_USER"})
	public String accessDenied() {
		return "inaccessible";
	}
	
	@RequestMapping("login")
	public String login() {
		return "showing login page";
	}
}
