package com.medic.reports.controller;
import com.medic.reports.exception.PatientNotFoundException;
import com.medic.reports.model.Report;
import com.medic.reports.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(
            description = "Generate a report of patient  via it's id "
    )
    @GetMapping("/{patientId}")
    public ResponseEntity<Report> getReport(@PathVariable String patientId) throws PatientNotFoundException {
        return ResponseEntity.ok(reportService.generateReports(patientId));
    }
}



