package com.medic.reports.model;

import com.medic.reports.enums.Risk;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    private String patientId;

    private String firstName;

    private String lastName;

    private String gender;

    private String birthdate;

    private Integer age;

    private Risk risk;

    private List<String> triggers;
}
