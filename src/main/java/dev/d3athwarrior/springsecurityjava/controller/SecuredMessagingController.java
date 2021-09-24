/**
 * 
 */
package dev.d3athwarrior.springsecurityjava.controller;

import javax.annotation.security.RolesAllowed;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * @author administrator
 *
 */
@Controller
public class SecuredMessagingController {
	
	@RolesAllowed({"ROLE_USER"})
	@MessageMapping("/subscribe")
	public void openConnection() {
		System.out.println("Subscribed!");
	}
}
