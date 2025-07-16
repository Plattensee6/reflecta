package hu.test.reflecta.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.test.reflecta.auth.dto.LoginRequest;
import hu.test.reflecta.auth.dto.LoginResponse;
import hu.test.reflecta.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(classes = hu.test.reflecta.auth.service.AuthServiceImpl.class)
@TestPropertySource("classpath:application-test.properties")
public class AuthControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void login_ShouldReturn200_WhenLoginSucceeds() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("john", "password");
        LoginResponse response = new LoginResponse("fake-jwt-token", 1001L, 1111L);

        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void login_ShouldReturn401_WhenLoginFails() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("john", "wrongpassword");

        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
