package hu.test.reflecta.meeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.test.reflecta.ReflectaApplication;
import hu.test.reflecta.meeting.data.dto.MeetingRequest;
import hu.test.reflecta.meeting.data.dto.MeetingResponse;
import hu.test.reflecta.user.data.model.Position;
import hu.test.reflecta.user.data.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import hu.test.reflecta.user.data.model.User;
import java.time.LocalDateTime;

@SpringBootTest(classes = {ReflectaApplication.class})
@AutoConfigureMockMvc
@Testcontainers
public class MeetingIntegrationTest {

    private static final Long MANAGER_ID = 1001L;
    private static final Long EMPLOYEE_ID = 1002L;
    private static final Long CURRENT_USER_ID = 1001L;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private Long managerId;
    private Long employeeId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Létrehozunk teszt usereket
        User manager = User.builder()
                .name("Manager One")
                .email("manager@example.com")
                .dateOfBirth(LocalDateTime.now().minusYears(20L))
                .position(Position.MANAGER)
                .build();
        managerId = userRepository.save(manager).getId();

        User employee = User.builder()
                .name("Employee One")
                .email("employee@example.com")
                .dateOfBirth(LocalDateTime.now().minusYears(20L))
                .position(Position.EMPLOYEE)
                .build();
        employeeId = userRepository.save(employee).getId();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"WRITE"})
    void shouldCreateMeeting() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusHours(1);

        MeetingRequest request = MeetingRequest.builder()
                .title("Test Meeting")
                .startTime(start)
                .endTime(end)
                .managerId(MANAGER_ID)
                .employeeId(EMPLOYEE_ID)
                .description("test description")
                .build();

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.meetingId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Meeting"))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        MeetingResponse response = objectMapper.readValue(json, MeetingResponse.class);

        Assertions.assertThat(response.getMeetingId()).isNotNull();
    }

    @Test
    void shouldFinalizeMeeting() throws Exception {
        // Create meeting first
        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusHours(1);
        MeetingRequest request = MeetingRequest.builder()
                .title("Meeting To Finalize")
                .startTime(start)
                .endTime(end)
                .managerId(MANAGER_ID)
                .employeeId(EMPLOYEE_ID)
                .description("desc")
                .build();

        String createJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long meetingId = objectMapper.readValue(createJson, MeetingResponse.class).getMeetingId();

        // Finalize
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/meetings/{id}/finalize", meetingId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Get and check
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/meetings/{id}", meetingId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isFinalized").value(true));
    }

    @Test
    void shouldListMeetings() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/meetings")
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }
}


