package homerep.homerepoauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationServerConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAccessTokenEndpoint() throws Exception {
        // Выполняем запрос на получение токена с корректными учетными данными клиента
        mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("homerep-admin-client", "yo78$$dontCrackPlease")) // client_id и client_secret
                        .param("grant_type", "client_credentials")) // Тип grant_type
                .andExpect(status().isOk()); // Ожидаем успешный ответ
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        // Выполняем запрос на получение токена с неверными учетными данными клиента
        mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("invalid-client", "invalid-secret")) // Неверные client_id и client_secret
                        .param("grant_type", "client_credentials")) // Тип grant_type
                .andExpect(status().isUnauthorized()); // Ожидаем ошибку 401 Unauthorized
    }
}