package edu.unac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.unac.domain.Device;
import edu.unac.domain.DeviceStatus;
import edu.unac.domain.Loan;
import edu.unac.repository.DeviceRepository;
import edu.unac.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private LoanRepository loanRepo;

    @BeforeEach
    void cleanDB() {
        deviceRepo.deleteAll();
    }

    @Test
    void saveDevice() throws Exception {
        var newDevice = new Device(null, "Smartphone", "Mobile Devices", "Tech Storage", DeviceStatus.AVAILABLE, System.currentTimeMillis());

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDevice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Smartphone")));
    }

    @Test
    void saveDeviceInvalid() throws Exception {
        var brokenDevice = new Device(null, null, "Mobile Devices", "Tech Storage", DeviceStatus.AVAILABLE, System.currentTimeMillis());

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brokenDevice)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listDevices() throws Exception {
        deviceRepo.save(new Device(null, "Smartphone", "Mobile Devices", "Tech Storage", DeviceStatus.AVAILABLE, System.currentTimeMillis()));
        deviceRepo.save(new Device(null, "Projector", "Multimedia", "Conference Room", DeviceStatus.AVAILABLE, System.currentTimeMillis()));

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getById() throws Exception {
        var saved = deviceRepo.save(new Device(null, "Smartphone", "Mobile Devices", "Tech Storage", DeviceStatus.AVAILABLE, System.currentTimeMillis()));

        mockMvc.perform(get("/api/devices/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Smartphone")));
    }

    @Test
    void changeStatus() throws Exception {
        var saved = deviceRepo.save(new Device(null, "Smartphone", "Mobile Devices", "Tech Storage", DeviceStatus.AVAILABLE, System.currentTimeMillis()));

        mockMvc.perform(put("/api/devices/" + saved.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", DeviceStatus.LOANED.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("LOANED")));
    }

    @Test
    void changeStatusNotFound() throws Exception {
        mockMvc.perform(put("/api/devices/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", DeviceStatus.LOANED.name()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDevice() throws Exception {
        var saved = deviceRepo.save(new Device(null, "Smartphone", "Mobile Devices", "Tech Storage", DeviceStatus.AVAILABLE, System.currentTimeMillis()));

        mockMvc.perform(delete("/api/devices/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWithLoans() throws Exception {
        var device = deviceRepo.save(new Device(null, "Smartphone", "Mobile Devices", "Tech Storage", DeviceStatus.AVAILABLE, System.currentTimeMillis()));
        loanRepo.save(new Loan(null, device.getId(), "Mark Evans", System.currentTimeMillis(), System.currentTimeMillis() + 100000, false));

        mockMvc.perform(delete("/api/devices/" + device.getId()))
                .andExpect(status().isConflict());
    }
}
