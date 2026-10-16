package cz.ecis.api.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import cz.ecis.api.SecController;
import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.db.repo.UserRepository;
import cz.ecis.model.dto.LoginRequestDto;
import cz.ecis.model.dto.LoginResponseDto;
import cz.ecis.model.dto.UserPasswordChangeDto;

public class PasswordChangeTest extends EcisIntegrationTestClient {

	private static final Long TEST_USER_ID = 1L;
	private static final Long OTHER_USER_ID = 2L;
	private static final String NEW_PASSWORD = "new-integration-password-2026";

	@Autowired
	private UserRepository userRepository;

	@Test
	@Order(1)
	void incorrectCurrentPasswordReturnsBadRequest() throws Exception {
		this.withExpectedStatus(HttpStatus.BAD_REQUEST).runPatch(
			this.passwordPath(TEST_USER_ID),
			"test",
			this.passwordChangeRequest("test", NEW_PASSWORD, "wrong-current-password"),
			Object.class
		);
		assertThat(this.userRepository.findById(TEST_USER_ID).orElseThrow().getVersion()).isZero();
	}

	@Test
	@Order(2)
	void mismatchedUsernameReturnsNotFound() throws Exception {
		this.withExpectedStatus(HttpStatus.NOT_FOUND).runPatch(
			this.passwordPath(TEST_USER_ID),
			"test",
			this.passwordChangeRequest("other", NEW_PASSWORD, "test"),
			Object.class
		);
		assertThat(this.userRepository.findById(TEST_USER_ID).orElseThrow().getVersion()).isZero();
	}

	@Test
	@Order(3)
	void changingAnotherUsersPasswordAsRegularUserReturnsNotFound() throws Exception {
		this.withExpectedStatus(HttpStatus.NOT_FOUND).runPatch(
			this.passwordPath(OTHER_USER_ID),
			"test",
			this.passwordChangeRequest("other", NEW_PASSWORD, "test"),
			Object.class
		);

		LoginResponseDto login = this.runPost(
			SecController.PATH_LOGIN,
			null,
			new LoginRequestDto("other", "test"),
			LoginResponseDto.class
		);
		assertThat(login.getToken()).isNotBlank();
	}

	@Test
	@Order(4)
	void passwordTooShortReturnsBadRequest() throws Exception {
		this.withExpectedStatus(HttpStatus.BAD_REQUEST).runPatch(
			this.passwordPath(TEST_USER_ID),
			"test",
			this.passwordChangeRequest("test", "too-short", "test"),
			Object.class
		);
	}

	@Test
	@Order(5)
	void missingRequiredFieldsReturnsUnprocessableContent() throws Exception {
		this.withExpectedStatus(HttpStatus.UNPROCESSABLE_CONTENT).runPatch(
			this.passwordPath(TEST_USER_ID),
			"test",
			new UserPasswordChangeDto(),
			Object.class
		);
	}

	@Test
	@Order(6)
	void unauthenticatedPasswordChangeReturnsUnauthorized() throws Exception {
		this.withExpectedStatus(HttpStatus.UNAUTHORIZED).runPatch(
			this.passwordPath(TEST_USER_ID),
			null,
			this.passwordChangeRequest("test", NEW_PASSWORD, "test"),
			Object.class
		);
	}

	@Test
	@Order(7)
	void systemAdministratorCanChangeAnotherUsersPassword() throws Exception {
		LoginResponseDto adminLogin = this.runPost(
			SecController.PATH_LOGIN,
			null,
			new LoginRequestDto("admin", "test"),
			LoginResponseDto.class
		);
		this.withUserToken("admin", "Bearer " + adminLogin.getToken());
		String otherUserPassword = "admin-changed-password-2026";

		this.withExpectedEmptyContent().runPatch(
			this.passwordPath(OTHER_USER_ID),
			"admin",
			this.passwordChangeRequest("other", otherUserPassword, null),
			Object.class
		);

		LoginResponseDto otherUserLogin = this.runPost(
			SecController.PATH_LOGIN,
			null,
			new LoginRequestDto("other", otherUserPassword),
			LoginResponseDto.class
		);
		assertThat(otherUserLogin.getToken()).isNotBlank();
	}

	@Test
	@Order(8)
	void systemAdministratorMustProvideCurrentPasswordWhenChangingOwnPassword() throws Exception {
		LoginResponseDto adminLogin = this.runPost(
			SecController.PATH_LOGIN,
			null,
			new LoginRequestDto("admin", "test"),
			LoginResponseDto.class
		);
		this.withUserToken("admin", "Bearer " + adminLogin.getToken());

		this.withExpectedStatus(HttpStatus.BAD_REQUEST).runPatch(
			this.passwordPath(3L),
			"admin",
			this.passwordChangeRequest("admin", "admin-own-new-password-2026", null),
			Object.class
		);
 	}

	@Test
	@Order(9)
	void changeOwnPasswordReturnsNoContentAndNewPasswordWorks() throws Exception {
		this.withExpectedEmptyContent().runPatch(
			this.passwordPath(TEST_USER_ID),
			"test",
			this.passwordChangeRequest("test", NEW_PASSWORD, "test"),
			Object.class
		);

		LoginResponseDto login = this.runPost(
			SecController.PATH_LOGIN,
			null,
			new LoginRequestDto("test", NEW_PASSWORD),
			LoginResponseDto.class
		);
		assertThat(login.getToken()).isNotBlank();
	}

	private String passwordPath(Long userId) {
		this.addUrlParam("id", userId);
		return "/user/{id}/password";
	}

	private UserPasswordChangeDto passwordChangeRequest(String username, String password, String actualPassword) {
		UserPasswordChangeDto request = new UserPasswordChangeDto();
		request.setUsername(username);
		request.setPassword(password);
		request.setActualPassword(actualPassword);
		return request;
	}
}
