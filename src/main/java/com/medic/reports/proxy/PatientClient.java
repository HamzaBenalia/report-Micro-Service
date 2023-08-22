package com.medic.reports.proxy;

import com.medic.reports.model.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patients", url = "${patient.url}")

public interface PatientClient {


    @GetMapping("/patient/{id}")
    Patient getPatientById(@PathVariable("id") String id);
}
