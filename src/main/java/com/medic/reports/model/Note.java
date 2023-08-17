package com.medic.reports.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note {

    private String id;

    private LocalDate date;

    private String patientId;

    private String content;
}
