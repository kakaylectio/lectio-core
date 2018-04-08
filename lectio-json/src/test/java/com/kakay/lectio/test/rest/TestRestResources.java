package com.kakay.lectio.test.rest;

import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.google.common.net.HttpHeaders;
import com.kakay.lectio.auth.EmailPassword;
import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.rest.LectioRestApplication;
import com.kakay.lectio.rest.LectioRestConfiguration;
import com.kakay.lectio.test.scenarios.ClearData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.User;

import io.dropwizard.testing.junit.DropwizardAppRule;

public abstract class TestRestResources {

	protected static String appUri = "http://localhost:8888";
	@Rule
	public DropwizardAppRule<LectioRestConfiguration> appRule = 
	   new DropwizardAppRule<LectioRestConfiguration>(LectioRestApplication.class, "lectio-rest.yml");
	protected Notebook notebook;
	protected User teacher;
	protected SeedData seedData;

	protected String savedTokenString;

	public TestRestResources() {
		super();
	}

	abstract protected SeedData getSeedData();

	abstract protected Object getResource();

	@Before
	public void setUp() throws Exception {
		appUri = "http://localhost:" + appRule.getPort(0);

		seedData = getSeedData();

		teacher = seedData.getTeacher();
		notebook = seedData.getNotebook();

		// Set up the rest client authentication
		Map<String, String> emailToPasswordMap = seedData.getEmailToPasswordMap();
		String teacherPassword = emailToPasswordMap.get(teacher.getEmail());
		// teacherClient = resources.client();

		LoginResponse loginResponse = getLoginResponse(teacher.getEmail(), teacherPassword, "/login");
		savedTokenString = loginResponse.getToken();
	}

	@After
	public void tearDown() throws Exception {
		ClearData.main(new String[] {});
	}

	protected String hitEndpoint(String targetString) {
		// Hit the endpoint and get the raw json string
		String resp = appRule.client().target(appUri + targetString).request()
				.header(HttpHeaders.AUTHORIZATION, "Token " + savedTokenString).get(String.class);

		return resp;
	}

	protected LoginResponse getLoginResponse(String teacherEmail, String teacherPassword, String targetString) {

		EmailPassword emailPassword = new EmailPassword();
		emailPassword.setEmail(teacherEmail);
		emailPassword.setPassword(teacherPassword);

		// Hit the endpoint and get the raw json string
		Client client = appRule.client();
		Entity entity = Entity.json(emailPassword);
		Response resp = client.target(appUri + targetString).request(MediaType.APPLICATION_JSON).post(entity);
		if (resp.getStatus() >= 400) {
			throw new WebApplicationException(resp);
		}
		LoginResponse loginResponse = resp.readEntity(LoginResponse.class);
		return loginResponse;
	}
	
	protected Response postEndpoint(String targetString, Object bodyContent) {
		Client client = appRule.client();
		Entity entity = Entity.json(bodyContent);
		Response resp = client.target(appUri + targetString)
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Token " + savedTokenString)
				.post(entity);
		return resp;
		
	}
	
	

}