package edu.unac.controller;

import edu.unac.domain.Device;
import edu.unac.domain.Loan;
import edu.unac.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.unac.domain.DeviceStatus;
import edu.unac.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LoanRepository loanData;

    @Autowired
    private DeviceRepository deviceData;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void cleanDb() {
        loanData.deleteAll();
        deviceData.deleteAll();
    }

    @Test
    void createLoan() throws Exception {
        Device dev = deviceData.save(
                new Device(null, "Printer Pro", "Administration", "Main Storage", DeviceStatus.AVAILABLE, System.currentTimeMillis())
        );

        Loan loan = new Loan(null, dev.getId(), "Laura Gomez",
                System.currentTimeMillis(), System.currentTimeMillis() + 604800000L, false);

        mvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.borrowedBy", is("Laura Gomez")))
                .andExpect(jsonPath("$.deviceId", is(dev.getId().intValue())));
    }

    @Test
    void createLoanWithInvalidDevice() throws Exception {
        Loan loan = new Loan(null, 999L, "John Doe",
                System.currentTimeMillis(), System.currentTimeMillis() + 86400000L, false);

        mvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loan)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllLoans() throws Exception {
        Device d1 = deviceData.save(new Device(null, "Barcode Scanner", "IT Department", "Shelf A1", DeviceStatus.AVAILABLE, System.currentTimeMillis()));
        Device d2 = deviceData.save(new Device(null, "LCD Monitor", "IT Department", "Shelf B1", DeviceStatus.AVAILABLE, System.currentTimeMillis()));

        loanData.save(new Loan(null, d1.getId(), "Carlos Smith", System.currentTimeMillis(), System.currentTimeMillis() + 604800000L, false));
        loanData.save(new Loan(null, d2.getId(), "Carlos Smith", System.currentTimeMillis(), System.currentTimeMillis() + 604800000L, false));

        mvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getLoanById() throws Exception {
        Device d = deviceData.save(new Device(null, "Android Tablet", "Mobile Devices", "Room 101", DeviceStatus.AVAILABLE, System.currentTimeMillis()));
        Loan loan = loanData.save(new Loan(null, d.getId(), "Anna Brown", System.currentTimeMillis(), System.currentTimeMillis() + 604800000L, false));

        mvc.perform(get("/api/loans/" + loan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowedBy", is("Anna Brown")))
                .andExpect(jsonPath("$.deviceId", is(d.getId().intValue())));
    }

    @Test
    void returnLoan() throws Exception {
        Device d = deviceData.save(new Device(null, "Digital Camera", "Multimedia", "Media Closet", DeviceStatus.AVAILABLE, System.currentTimeMillis()));
        Loan loan = loanData.save(new Loan(null, d.getId(), "Mario Johnson", System.currentTimeMillis(), System.currentTimeMillis() + 604800000L, false));

        mvc.perform(put("/api/loans/" + loan.getId() + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returned", is(true)))
                .andExpect(jsonPath("$.deviceId", is(d.getId().intValue())));
    }

    @Test
    void returnNonExistentLoan() throws Exception {
        mvc.perform(put("/api/loans/999/return"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnAlreadyReturnedLoan() throws Exception {
        Loan loan = loanData.save(new Loan(null, null, "Jane Wilson", System.currentTimeMillis(), System.currentTimeMillis() + 604800000L, true));

        mvc.perform(put("/api/loans/" + loan.getId() + "/return"))
                .andExpect(status().isConflict());
    }

    @Test
    void getLoansByDevice() throws Exception {
        Device d = deviceData.save(new Device(null, "WiFi Router", "Networking", "Server Room", DeviceStatus.AVAILABLE, System.currentTimeMillis()));
        loanData.save(new Loan(null, d.getId(), "Sandra Lee", System.currentTimeMillis(), System.currentTimeMillis() + 604800000L, false));

        mvc.perform(get("/api/loans/device/" + d.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}