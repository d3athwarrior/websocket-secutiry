/**
 * 
 */
package dev.d3athwarrior.springsecurityjava;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosTicketValidator;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * @author Anish.Kumar
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${app.service-principal}")
	private String servicePrincipal;
	
	@Value("${app.keytab-location}")
	private String serviceKeytab;
	public WebSecurityConfig() {
		super(true);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http/*
			 * .sessionManagement()
			 * .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) .and()
			 */
		.csrf()
		.disable()
			.exceptionHandling()
				.authenticationEntryPoint(spnegoEntryPoint())
			.and()
			.authorizeRequests()
//			.antMatchers("/", "/home", "/error").permitAll()
				.antMatchers("/login").permitAll()
				.anyRequest().authenticated()
			.and()
//			.formLogin().successForwardUrl("/inaccessible")
//			.and()
//			.logout().permitAll()
//			.and()
			.addFilterBefore(spnegoAuthenticationProcessingFilter(), BasicAuthenticationFilter.class);
	}
	
	@Bean
	public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter() {
		SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
		try {
			AuthenticationManager authenticationManager = authenticationManagerBean();
			filter.setAuthenticationManager(authenticationManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filter;
	}

	@Bean
	public SpnegoEntryPoint spnegoEntryPoint() {
		return new SpnegoEntryPoint("/login");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.authenticationProvider(kerberosAuthenticationProvider())
			.authenticationProvider(kerberosServiceAuthenticationProvider());
	}
	
	/*
	 * @Autowired public void configureGlobal(AuthenticationManagerBuilder auth)
	 * throws Exception {
	 * auth.authenticationProvider(kerberosAuthenticationProvider())
	 * .authenticationProvider(kerberosServiceAuthenticationProvider()); }
	 */
	
	@Bean
	public KerberosAuthenticationProvider kerberosServiceAuthenticationProvider() {
		KerberosAuthenticationProvider provider = new KerberosAuthenticationProvider();
	    SunJaasKerberosClient client = new SunJaasKerberosClient();
	    client.setDebug(true);
	    provider.setKerberosClient(client);
	    provider.setUserDetailsService(dummyUserDetailsService());
	    return provider;
	}

	@Bean
	public UserDetailsService dummyUserDetailsService() {
		return new DummyUserDetailsService();
	}

	@Bean
	public KerberosServiceAuthenticationProvider kerberosAuthenticationProvider() {
		KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
	    provider.setTicketValidator(sunJaasKerberosTicketValidator());
	    provider.setUserDetailsService(dummyUserDetailsService());
	    return provider;
	}

	@Bean
	public KerberosTicketValidator sunJaasKerberosTicketValidator() {
		SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
	    ticketValidator.setServicePrincipal(this.servicePrincipal);
	    ticketValidator.setKeyTabLocation(new FileSystemResource(this.serviceKeytab));
	    ticketValidator.setDebug(true);
	    return ticketValidator;
	}
}
