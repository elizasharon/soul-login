package com.loginportal.login.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.loginportal.login.model.LoginStatus;
import com.loginportal.utils.encoder.CustomPasswordEncoder;
import com.loginportal.utils.model.AccountStatus;
import com.loginportal.utils.model.LoginAttempts;
import com.loginportal.utils.model.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@EnableSwagger2
@Api(value = "Operations pertaining to login")
public class LoginController {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	CustomPasswordEncoder customPasswordEncoder;

	private Optional<LoginAttempts> optional; // will be assigned in "isUserBlocked" check

	private static Logger logger = LoggerFactory.getLogger(LoginController.class);
	
//	private static final Integer numAttempts = Integer.valueOf("${LoginController.login.attempts}");

	@PostMapping("/login")
	@ApiOperation(value = "${LoginController.login.value}", notes = "${LoginController.login.notes}", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "SUCCESS | OK"),
			@ApiResponse(code = 404, message = "NOT FOUND") })
	public LoginStatus authenticate(
			@ApiParam(value = "${LoginController.login.param}", required = true) @RequestBody User loginUser) {
		String url = "http://localhost:8017/api/data/user/find";
		User user = new User();
		// System.out.println("================="+loginUser.getEmailID());

		if (loginUser != null) {
			if (loginUser.getUserID() != null)
				user.setUserID(loginUser.getUserID());
			else
				user.setEmailID(loginUser.getEmailID());
		}

		HttpEntity<User> httpEntity = new HttpEntity<>(user);
		ResponseEntity<Optional<User>> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<Optional<User>>() {
				});
		Optional<User> optional = response.getBody();
		return authenticateUser(optional, loginUser);
	}

	public LoginStatus authenticateUser(Optional<User> optional, User loginUser) {
		LoginStatus loginStatus = new LoginStatus();

		if (optional.isPresent()) {
			logger.info("user is present. ID: {}", optional.get().getUserID());
			User user = optional.get();
			System.out.println("111111111111111111" + user.getAccountStatus());
			if (isUserBlocked(user)) {
				logger.info("User is blocked");
				loginStatus = ifUserGetsBlocked(user.getUserID());
			} else {
				String storedHashedPassword = user.getPasswordHistory().getPwd1();
				String storedSalt = user.getPasswordHistory().getSalt1();
				String inputHashedPassword = customPasswordEncoder
						.encodeWithSalt(loginUser.getPasswordHistory().getPwd1(), storedSalt);

				if (inputHashedPassword.equals(storedHashedPassword)) {
					if (user.getAccountStatus() == AccountStatus.ACTIVE) {

						loginStatus.setUserID(user.getUserID());
						loginStatus.setLoginStatusMessage("Authenticated");
						resetLoginAttempts(user.getUserID());
					} else {
						loginStatus.setUserID(user.getUserID());
						loginStatus.setLoginStatusMessage("Not a confirmed user");
						resetLoginAttempts(user.getUserID());
					}
				} else {
					loginStatus = updateLoginAttempts(user.getUserID());
				}
			}
		} else {
			loginStatus.setLoginStatusMessage("User doesn't exist");
		}

		return loginStatus;
	}

	@PostMapping("/google")
	public LoginStatus socialLogin(@RequestBody User loginUser) {

		System.out.println("22222222222222222222222 : " + loginUser);
		String url = "http://localhost:8017/api/data/user/find";
		User user = new User();
		LoginStatus loginStatus = new LoginStatus();

		if (loginUser != null) {
			user.setEmailID(loginUser.getEmailID());
		}
		System.out.println("a" + user);
		System.out.println("b" + loginUser);
		HttpEntity<User> httpEntity = new HttpEntity<>(user);
		System.out.println("HTTP ENTITY=========" + httpEntity);
		ResponseEntity<Optional<User>> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<Optional<User>>() {
				});

		System.out.println("!!!!!!!!!!!!!!!!!!!!MUST BE PRINTED AFTER URL");
		Optional<User> optional = response.getBody();
		System.out.println(optional);
		if (optional.isPresent()) {
			User dbuser = optional.get();

			if (user.getEmailID().equals(dbuser.getEmailID())) {
				System.out.println("if present" + dbuser.getUserID());
				loginStatus.setLoginStatusMessage("Authenticated");
				loginStatus.setUserID(dbuser.getUserID());

			}
		} else {
			System.out.println("not present");
			loginStatus.setLoginStatusMessage("User doesn't exist");
		}
		System.out.println("=========================" + loginStatus.getUserID());

		return loginStatus;
	}

	public LoginStatus ifUserGetsBlocked(Long userID) {
		LoginStatus loginStatus = new LoginStatus();
		loginStatus.setUserID(userID);
		String url = "http://localhost:8017/api/data/loginattempts/update";

		if (getDaysSinceLastAttempt(userID) < 1) {
			logger.info("User is blocked. Days: {}", getDaysSinceLastAttempt(userID));
			loginStatus.setLoginStatusMessage("Blocked user, please try after one day");
		} else {
			logger.info("User is blocked. Days: {}", getDaysSinceLastAttempt(userID));
			loginStatus.setLoginStatusMessage("Confirm login using OTP");

			LoginAttempts loginAttempts = optional.get();
			loginAttempts.setBlocked(false);

			HttpEntity<LoginAttempts> httpEntity = new HttpEntity<>(loginAttempts);
			ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
					new ParameterizedTypeReference<Integer>() {
					});
		}

		return loginStatus;
	}

	public void resetLoginAttempts(Long userID) {
		logger.info("Reseting LoginAttempts");
		String url = "http://localhost:8017/api/data/loginattempts/delete";
		HttpEntity<User> httpEntity = new HttpEntity<>(new User(userID));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<String>() {
				});
	}

	private LoginStatus updateLoginAttempts(Long userID) {
		String url;
		LoginAttempts loginAttempts;
		LoginStatus loginStatus = new LoginStatus();
		loginStatus.setUserID(userID);

		if (optional.isPresent()) {
			logger.info("LoginAttempt is present. Updating it");
			url = "http://localhost:8017/api/data/loginattempts/update";

			loginAttempts = optional.get();
			int numberOfAttempts = loginAttempts.getNumberOfAttempts();
			numberOfAttempts++;

			if (numberOfAttempts < 3) {
				logger.info("less than {} attempts", 3);
				loginAttempts.setNumberOfAttempts(numberOfAttempts);
				loginStatus.setLoginStatusMessage("Incorrect password");
			} else {
				logger.info("3rd attempt");
				loginAttempts.setNumberOfAttempts(0);
				loginAttempts.setBlocked(true);
				loginStatus.setLoginStatusMessage("Incorrect password -- 3rd incorrect attempt -- You're blocked");
			}

			HttpEntity<LoginAttempts> httpEntity = new HttpEntity<>(loginAttempts);
			ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
					new ParameterizedTypeReference<Integer>() {
					});
		} else { // no record; new user
			logger.info("no LoginAttempt exist. So creating one");
			url = "http://localhost:8017/api/data/loginattempts/create";
			loginAttempts = new LoginAttempts();
			loginAttempts.setUserID(userID);
			loginAttempts.setNumberOfAttempts(1);

			loginStatus.setLoginStatusMessage("Incorrect password");

			HttpEntity<LoginAttempts> httpEntity = new HttpEntity<>(loginAttempts);
			ResponseEntity<LoginAttempts> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
					new ParameterizedTypeReference<LoginAttempts>() {
					});
		}

		return loginStatus;
	}

	public Boolean isUserBlocked(User user) {
		String url = "http://localhost:8017/api/data/loginattempts/find";
		HttpEntity<User> httpEntity = new HttpEntity<>(new User(user.getUserID()));
		ResponseEntity<Optional<LoginAttempts>> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<Optional<LoginAttempts>>() {
				});

		optional = response.getBody();

		if (optional.isPresent()) {
			LoginAttempts loginAttempts = optional.get();
			return loginAttempts.getBlocked();
		} else { // no record; new user; so not blocked
			return false;
		}
	}

	public Long getDaysSinceLastAttempt(Long userID) {
		LoginAttempts loginAttempts = optional.get();
		LocalDateTime now = LocalDateTime.now();
		return ChronoUnit.DAYS.between(loginAttempts.getLastAttempt(), now);
	}
}
