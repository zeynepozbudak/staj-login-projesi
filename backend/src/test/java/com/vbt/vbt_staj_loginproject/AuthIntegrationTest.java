package com.vbt.vbt_staj_loginproject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)  //rastgele bir portta başlatır
@ActiveProfiles("test")  //gerçek postgresql yerine h2 kullanıyor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static String refreshTokenCookie;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }


    @Test
    @Order(1)
    @DisplayName("Register — başarılı kayıt 201 dönmeli")
    void shouldRegisterSuccessfully() throws Exception {
        //json body hazırlanır
        String body = """
                {
                    "firstName": "Meryem",
                    "lastName": "Test",
                    "email": "testtest@test.com",
                    "password": "password123"
                }
                """;

        //http isteği oluştururlur
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/register")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        //istek gönderilir cevap alınır
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(201);

        //body kontrol edilir
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("email").asText()).isEqualTo("testtest@test.com");
        assertThat(json.get("firstName").asText()).isEqualTo("Meryem");
        assertThat(json.has("accessToken")).isTrue();

        //cookie saklanır
        String setCookie = response.headers().firstValue("Set-Cookie").orElse(null);
        assertThat(setCookie).isNotNull();
        refreshTokenCookie = setCookie.split(";")[0];
    }

    @Test
    @Order(2)
    @DisplayName("Register — aynı email ile tekrar kayıt 409 dönmeli")
    void shouldReturnConflictWhenEmailExists() throws Exception {
        String body = """
                {
                    "firstName": "Meryem",
                    "lastName": "Test",
                    "email": "testtest@test.com",
                    "password": "password123"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/register")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(409);
    }

    @Test
    @Order(3)
    @DisplayName("Register — eksik alanlarla 400 dönmeli")
    void shouldReturnBadRequestWhenFieldsMissing() throws Exception {
        String body = """
                {
                    "firstName": "Meryem",
                    "lastName": "Test",
                    "email": "",
                    "password": "password123"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/register")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }


    @Test
    @Order(4)
    @DisplayName("Login — doğru bilgilerle 200 dönmeli")
    void shouldLoginSuccessfully() throws Exception {
        String body = """
                {
                    "email": "testtest@test.com",
                    "password": "password123"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/login")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("email").asText()).isEqualTo("testtest@test.com");
        assertThat(json.has("accessToken")).isTrue();

        //yeni cookie saklanır
        String setCookie = response.headers().firstValue("Set-Cookie").orElse(null);
        refreshTokenCookie = setCookie.split(";")[0];
    }

    @Test
    @Order(5)
    @DisplayName("Login — yanlış şifreyle 401 dönmeli")
    void shouldReturnUnauthorizedWithWrongPassword() throws Exception {
        String body = """
                {
                    "email": "testtest@test.com",
                    "password": "yanlis-sifre"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/login")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    @Order(6)
    @DisplayName("Login — olmayan email ile 401 dönmeli")
    void shouldReturnUnauthorizedWithNonExistentEmail() throws Exception {
        String body = """
                {
                    "email": "yok@test.com",
                    "password": "password123"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/login")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    @Order(7)
    @DisplayName("Refresh — geçerli cookie ile yeni access token dönmeli")
    void shouldRefreshSuccessfully() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/refresh")))
                .header("Cookie", refreshTokenCookie)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.has("accessToken")).isTrue();

        String setCookie = response.headers().firstValue("Set-Cookie").orElse(null);
        refreshTokenCookie = setCookie.split(";")[0];
    }

    @Test
    @Order(8)
    @DisplayName("Refresh — cookie olmadan 401 dönmeli")
    void shouldReturnUnauthorizedWithoutCookie() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/refresh")))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }


    @Test
    @Order(9)
    @DisplayName("Logout — 200 dönmeli")
    void shouldLogoutSuccessfully() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/logout")))
                .header("Cookie", refreshTokenCookie)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(10)
    @DisplayName("Logout sonrası refresh — 401 dönmeli")
    void shouldReturnUnauthorizedAfterLogout() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url("/auth/refresh")))
                .header("Cookie", refreshTokenCookie)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }
}