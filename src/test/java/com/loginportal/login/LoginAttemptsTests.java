//package com.loginportal.login;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//import java.io.IOException;
//import java.util.Optional;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.loginportal.login.model.CustomPasswordEncoder;
//import com.loginportal.login.model.LoginStatus;
//import com.loginportal.login.model.PasswordHistory;
//import com.loginportal.login.model.User;
//
//@RunWith(SpringRunner.class)
//public class LoginAttemptsTests {
//
//	@Autowired
//	WebApplicationContext webApplicationContext;
//
//	@MockBean
//	private RestTemplate restTemplate;
//
//	private MockMvc mvc;
//	private CustomPasswordEncoder customPasswordEncoder;
//	
//	@Before
//	public void init() {
//		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//	}
//	
//	public String mapToJson(Object obj) throws JsonProcessingException {
//		ObjectMapper objectMapper = new ObjectMapper();
//		return objectMapper.writeValueAsString(obj);
//	}
//
//	public <T> T mapFromJson(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		return objectMapper.readValue(json, clazz);
//	}
//
//	
//	@Test
//	public void incorrectAttempt1() throws Exception {
//		User userToLoginService = new User();
//		userToLoginService.setUserID(1L);
//		PasswordHistory password = new PasswordHistory();
//		password.setPwd1("smallBall");
//		userToLoginService.setPasswordHistory(password);
//
//		User userToDataService = new User();
//		userToDataService.setUserID(1L);
//
//		User userFromDataService = new User(1L, "abc@gmail.com", new PasswordHistory(2L,
//				"$2a$12$2jDJzTrQ9UOP43LVEyrdweLBe10SA0csWa5EzsHlQm0suxaWv7UqS", "$2a$12$2jDJzTrQ9UOP43LVEyrdwe"));
//		Optional<User> optional = Optional.of(userFromDataService);
//
//		HttpEntity<User> httpEntity = new HttpEntity<User>(userToDataService);
//		when(restTemplate.exchange("http://localhost:8017/api/data/user", HttpMethod.POST, httpEntity,
//				new ParameterizedTypeReference<Optional<User>>() {
//				})).thenReturn(new ResponseEntity<>(optional, HttpStatus.OK));
//		
//		HttpEntity<Long> httpEntity1 = new HttpEntity<Long>(userToDataService.getUserID());
//		when(restTemplate.exchange("http://localhost:8017/api/data/updatefailattempt", HttpMethod.POST, httpEntity1,
//				new ParameterizedTypeReference<Boolean>() {
//				})).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
//
//		when(customPasswordEncoder.encodeWithSalt("smallBall", "$2a$12$2jDJzTrQ9UOP43LVEyrdwe"))
//				.thenReturn("$2a$12$2jDJzTrQ9UOP43LVEyrdweLBe10SA0csWa5EzsHlQm0suxaWv7UqT");
//
//		MvcResult mvcResult = mvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON)
//				.content(mapToJson(userToLoginService)).accept(MediaType.ALL_VALUE)).andReturn();
//
//		int apiCallStatus = mvcResult.getResponse().getStatus();
//		String apiCallResponseFromLoginService = mvcResult.getResponse().getContentAsString();
//		LoginStatus loginStatus = mapFromJson(apiCallResponseFromLoginService, LoginStatus.class);
//	
//		System.out.println(apiCallStatus);
//		assertEquals(200, apiCallStatus);
//		assertEquals("Incorrect password", loginStatus.getLoginStatusMessage());
//	}
//
//	
//}
