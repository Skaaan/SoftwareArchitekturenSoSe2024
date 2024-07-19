package com.microservices.inventory.integration;

import com.microservices.inventory.model.Inventory;
import com.microservices.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class InventoryControllerIntegrationTests {

    private static final DockerImageName MY_IMAGE = DockerImageName.parse("arm64v8/mysql:8.0")
            .asCompatibleSubstituteFor("mysql");

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(MY_IMAGE)
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
    }

    @Test
    void testIsInStock() throws Exception {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        inventoryRepository.save(inventory);

        mockMvc.perform(get("/api/inventory/1234567890")
                        .param("quantity", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testReduceStock() throws Exception {
        Inventory inventory = new Inventory(null, "1234567890", 10);
        inventoryRepository.save(inventory);

        HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
        MvcResult result = mockMvc.perform(get("/"))
                .andReturn();
        CsrfToken csrfToken = (CsrfToken) result.getRequest().getAttribute(CsrfToken.class.getName());

        mockMvc.perform(post("/api/inventory/reduce/1234567890")
                        .sessionAttr(HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN"), csrfToken)
                        .param("_csrf", csrfToken.getToken())
                        .param("quantity", "5"))
                .andExpect(status().isOk());

        Optional<Inventory> updatedInventory = inventoryRepository.findByIsbn("1234567890");
        assertTrue(updatedInventory.isPresent());
        assertEquals(5, updatedInventory.get().getQuantity());
    }
}
