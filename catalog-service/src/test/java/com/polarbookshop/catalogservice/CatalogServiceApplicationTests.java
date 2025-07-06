package com.polarbookshop.catalogservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polarbookshop.catalogservice.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

/*
 * we’ll use the @SpringBootTest annotation configured to provide a full Spring applica-
tion context, including a running server that exposes its services through a random
port (because it doesn’t matter which one).
 */
// Loads a full Spring web application context and a Servlet container listening on a random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Enables the “integration” profile to load configuration from
// application-integration.yml
@ActiveProfiles("integration")
// Activates automatic startup and cleanup of test containers
@Testcontainers
class CatalogServiceApplicationTests {

	// Utility to perform REST calls for testing
	@Autowired
	private WebTestClient webTestClient;
	// Defines a Keycloak container for testing
	/*
	 * The Keycloak configuration I provided in the JSON file includes the
	 * definition of a
	 * test Client (polar-test) that we can use to authenticate users via a username
	 * and pass-
	 * word directly, instead of going through the browser-based flow we implemented
	 * in Edge
	 * Service. In OAuth2, such a flow is called a Password Grant, and it’s not
	 * recommended for
	 * production use.
	 */
	@Container
	private static final KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:26.2.5")
			.withRealmImportFile("/test-realm-config.json");
	// Customer
	private static KeycloakToken bjornTokens;
	// Customer and employee
	private static KeycloakToken isabelleTokens;

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		// Overwrites the Keycloak Issuer URI configuration to point to the test
		// Keycloak instance
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
	}

	/*
	 * Let’s set up CatalogServiceApplicationTests to authenticate with Keycloak as
	 * Isabelle and Bjorn so that we can obtain the Access Tokens we need to call
	 * the Catalog
	 * Service’s protected endpoints. Keep in mind that Isabelle is both a customer
	 * and
	 * employee, whereas Bjorn is only a customer.
	 * 
	 */
	@BeforeAll
	static void generateAccessTokens() {
		// A WebClient used to call Keycloak
		WebClient webClient = WebClient.builder()
				.baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
		// Authenticates as Isabelle and obtains an Access Token
		isabelleTokens = authenticateWith("isabelle", "password", webClient);
		bjornTokens = authenticateWith("bjorn", "password", webClient);
	}

	@Test
	void whenGetRequestWithIdThenBookReturned() {
		var bookIsbn = "1231231230";
		var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		Book expectedBook = webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient
				.post()
				.uri("/books")
				// Sends a request to add a book to the catalog as an authenticated employee
				// user (Isabelle)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange()
				// The book has been successfully created (201).
				.expectStatus().isCreated()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPostRequestUnauthenticatedThen401() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");
		// Sends a request to add a book to the catalog as
		webTestClient
				.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				// The book has not been created because the user is not authenticated (401).
				.expectStatus().isUnauthorized();
	}

	@Test
	void whenPostRequestUnauthorizedThen403() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient
				.post()
				.uri("/books")
				// Sends a request to add a book to the catalog as an authenticated customer
				// user (Bjorn)
				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange()
				// The book has not been created because the user doesn’t have the correct
				// authorization, no “employee” role (403).
				.expectStatus().isForbidden();
	}

	@Test
	void whenPutRequestThenBookUpdated() {
		var bookIsbn = "1231231232";
		var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		Book createdBook = webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();
		var bookToUpdate = new Book(createdBook.id(), createdBook.isbn(), createdBook.title(), createdBook.author(),
				7.95,
				createdBook.publisher(), createdBook.createdDate(), createdBook.lastModifiedDate(),
				createdBook.createdBy(), createdBook.lastModifiedBy(), createdBook.version());

		webTestClient
				.put()
				.uri("/books/" + bookIsbn)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToUpdate)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
				});
	}

	@Test
	void whenDeleteRequestThenBookDeleted() {
		var bookIsbn = "1231231233";
		var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient
				.delete()
				.uri("/books/" + bookIsbn)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.exchange()
				.expectStatus().isNoContent();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class).value(errorMessage -> assertThat(errorMessage)
						.isEqualTo("The book with ISBN " + bookIsbn + " was not found."));
	}

	private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
		// Uses the Password Grant flow to authenticate with Keycloak directly
		return webClient
				.post()
				.body(BodyInserters.fromFormData("grant_type", "password")
						.with("client_id", "polar-test")
						.with("username", username)
						.with("password", password))
				.retrieve()
				.bodyToMono(KeycloakToken.class)
				// Blocks until a result is available. This is how we use WebClient imperatively
				// rather than reactively.
				.block();
	}

	private record KeycloakToken(String accessToken) {
		// Instructs Jackson to use this constructor when deserializing JSON into
		// KeycloakToken objects
		@JsonCreator
		private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
			this.accessToken = accessToken;
		}

	}

}