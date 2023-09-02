package com.medic.reports.controller;

import com.medic.reports.exception.PatientNotFoundException;
import com.medic.reports.model.Report;
import com.medic.reports.services.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)

public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    public void whenGetReport_givenValidPatientId_thenReturnsOkStatus() throws Exception {
        String patientId = "123";
        when(reportService.generateReports(patientId)).thenReturn(mock(Report.class));

        mockMvc.perform(get("/report/{patientId}", patientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reportService, times(1)).generateReports(patientId);
    }

    @Test
    public void whenGetReport_givenInvalidPatientId_thenReturnsNotFoundStatus() throws Exception {
        String patientId = "123";
        when(reportService.generateReports(patientId)).thenThrow(new PatientNotFoundException("Patient not found"));

        mockMvc.perform(get("/report/{patientId}", patientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
