package cz.ecis.api.sec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import cz.ecis.api.SecController;
import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.config.mail.MailConfig;
import cz.ecis.core.mail.MailService;
import cz.ecis.db.ent.PasswordResetEnt;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.db.repo.PasswordResetRepository;
import cz.ecis.db.repo.UserRepository;
import cz.ecis.model.dto.LoginRequestDto;
import cz.ecis.model.dto.LoginResponseDto;
import cz.ecis.model.dto.PasswordResetDto;
import cz.ecis.model.dto.RequestPasswordResetDto;

public class PasswordResetTest extends EcisIntegrationTestClient {

	private static final String EMAIL = "tester@bobovic.example";
	private static final String NEW_PASSWORD = "new-integration-password";
	private static final String RESET_TOKEN = "integration-test-reset-token";

	@Autowired
	private PasswordResetRepository passwordResetRepository;

	@Autowired
	private UserRepository userRepository;

	@MockitoSpyBean
	private MailService mailService;

	@Autowired
	@Qualifier(MailConfig.MAIL_TEMPLATE_ENGINE)
	private TemplateEngine mailTemplateEngine;

	@Test
    @Order(1)
	void requestPasswordReset() throws Exception {
		RequestPasswordResetDto request = new RequestPasswordResetDto();
		request.setEmail(EMAIL);

		this.withExpectedEmptyContent().runPost(
			SecController.PATH_REQUEST_PASSWORD_RESET,
			null,
			request,
			Object.class
		);

		UserEnt user = this.userRepository.findByEmailIgnoreCase(EMAIL).orElseThrow();
		PasswordResetEnt reset = this.passwordResetRepository.findFirstByUserIdOrderByGeneratedAtDesc(user.getId())
			.orElseThrow();

		ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
		verify(this.mailService).sendRender(
			eq(EMAIL),
			eq("email.password-reset.subject"),
			eq("password-reset"),
			contextCaptor.capture()
		);
		String emailHtml = this.mailTemplateEngine.process("password-reset", contextCaptor.getValue());
		String resetUrl = (String) contextCaptor.getValue().getVariable("resetUrl");
		assertThat(resetUrl).startsWith("http://localhost:4200/password-reset?token=");
		assertThat(resetUrl).endsWith("&email=" + EMAIL);
		String token = Arrays.stream(URI.create(resetUrl).getRawQuery().split("&"))
			.filter(parameter -> parameter.startsWith("token="))
			.map(parameter -> parameter.substring("token=".length()))
			.findFirst()
			.orElseThrow();
		assertThat(DigestUtils.sha256Hex(token)).isEqualTo(reset.getTokenHash());
		assertThat(emailHtml).contains(token, EMAIL, "Reset Password");
	}

	@Test
    @Order(2)
	void resetPassword() throws Exception {
		this.saveResetToken(RESET_TOKEN, OffsetDateTime.now().plusMinutes(20));

		PasswordResetDto request = new PasswordResetDto();
		request.setEmail(EMAIL);
		request.setToken(RESET_TOKEN);
		request.setPassword(NEW_PASSWORD);

		this.withExpectedEmptyContent().runPost(
			SecController.PATH_RESET_PASSWORD,
			null,
			request,
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

	@Test
	@Order(3)
	void requestPasswordResetForUnknownEmailReturnsNoContent() throws Exception {
		long resetCountBeforeRequest = this.passwordResetRepository.count();
		RequestPasswordResetDto request = new RequestPasswordResetDto();
		request.setEmail("unknown@bobovic.example");

		this.withExpectedEmptyContent().runPost(
			SecController.PATH_REQUEST_PASSWORD_RESET,
			null,
			request,
			Object.class
		);
		assertThat(this.passwordResetRepository.count()).isEqualTo(resetCountBeforeRequest);
	}

	@Test
	@Order(4)
	void resetPasswordWithUnknownTokenReturnsNoContent() throws Exception {
		long resetCountBeforeRequest = this.passwordResetRepository.count();
		PasswordResetDto request = this.passwordResetRequest("unknown-reset-token", NEW_PASSWORD);

		this.withExpectedEmptyContent().runPost(
			SecController.PATH_RESET_PASSWORD,
			null,
			request,
			Object.class
		);
		assertThat(this.passwordResetRepository.count()).isEqualTo(resetCountBeforeRequest);
	}

	@Test
	@Order(5)
	void resetPasswordWithShortPasswordReturnsBadRequest() throws Exception {
		PasswordResetDto request = this.passwordResetRequest(RESET_TOKEN, "too-short");

		this.withExpectedStatus(HttpStatus.BAD_REQUEST).runPost(
			SecController.PATH_RESET_PASSWORD,
			null,
			request,
			Object.class
		);
	}

	@Test
	@Order(6)
	void resetPasswordWithExpiredTokenReturnsBadRequest() throws Exception {
		String expiredToken = "expired-integration-test-reset-token";
		this.saveResetToken(expiredToken, OffsetDateTime.now().minusMinutes(1));
		PasswordResetDto request = this.passwordResetRequest(expiredToken, NEW_PASSWORD);

		this.withExpectedStatus(HttpStatus.BAD_REQUEST).runPost(
			SecController.PATH_RESET_PASSWORD,
			null,
			request,
			Object.class
		);
	}

	private void saveResetToken(String token, OffsetDateTime validTo) {
		UserEnt user = this.userRepository.findByEmailIgnoreCase(EMAIL).orElseThrow();
		OffsetDateTime now = OffsetDateTime.now();
		PasswordResetEnt reset = new PasswordResetEnt();
		reset.setUser(user);
		reset.setTokenHash(DigestUtils.sha256Hex(token));
		reset.setGeneratedAt(now);
		reset.setLastRequestedAt(now);
		reset.setValidTo(validTo);
		this.passwordResetRepository.save(reset);
	}

	private PasswordResetDto passwordResetRequest(String token, String password) {
		PasswordResetDto request = new PasswordResetDto();
		request.setEmail(EMAIL);
		request.setToken(token);
		request.setPassword(password);
		return request;
	}
}
