package org.internetstore.scootersrentapplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional // Will rollback changes to your Neon DB automatically!
public class ScooterControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testCreateScooter() throws Exception {
        String json = "{\"serialNumber\": \"CLOUD-123\", \"longitude\": 21.0122, \"latitude\": 52.2297}";

        mockMvc.perform(post("/api/scooters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serialNumber").value("CLOUD-123"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    public void testGetScooterById() throws Exception {
        String json = "{\"serialNumber\": \"CLOUD-456\", \"longitude\": 21.0122, \"latitude\": 52.2297}";
        
        String response = mockMvc.perform(post("/api/scooters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn().getResponse().getContentAsString();
                
        // Extract ID manually to bypass Jackson compiler bugs in SB 4.0.5
        String idStr = response.split("\"id\":")[1].split(",")[0].trim();

        mockMvc.perform(get("/api/scooters/" + idStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialNumber").value("CLOUD-456"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }
}
