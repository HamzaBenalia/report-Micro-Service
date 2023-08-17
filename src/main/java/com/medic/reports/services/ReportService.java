package com.medic.reports.services;
import com.medic.reports.enums.Risk;
import com.medic.reports.exception.PatientNotFoundException;
import com.medic.reports.model.Note;
import com.medic.reports.model.Patient;
import com.medic.reports.model.Report;
import com.medic.reports.proxy.NoteClient;
import com.medic.reports.proxy.PatientClient;
import com.medic.reports.triggers.Triggers;
import feign.FeignException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReportService {

    private final PatientClient patientClient;
    private final NoteClient noteClient;

    public ReportService(PatientClient patientClient, NoteClient noteClient) {
        this.patientClient = patientClient;
        this.noteClient = noteClient;
    }


    public Report generateReports(String patientId) throws PatientNotFoundException {
        if (patientId == null || patientId.isEmpty()) {
            throw new PatientNotFoundException("Patient with given ID was not found");
        }

        Patient patient;
        try {
            patient = patientClient.getPatientById(patientId);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new PatientNotFoundException("Could not retrieve patient with ID: " + patientId);
        }

        if (patient == null) {
            throw new PatientNotFoundException("Patient with given ID was not found");
        }

        int age = calculatePatientAge(patient.getDateDeNaissance());

        List<Note> notes;
        try {
            notes = noteClient.getNotesByPatientId(patientId);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not retrieve notes for patient with ID: " + patientId);
        }

        if (notes == null) {
            notes = Collections.emptyList();
        }

        List<String> triggersList = loadTriggersFromPatientNotes(notes);
        Risk risk = calculateRisk(triggersList.size(), age, patient.getGenre());

        return new Report(patientId, patient.getNom(), patient.getPrenom(), patient.getGenre(),
                patient.getDateDeNaissance(), age, risk, triggersList);
    }


    public Risk calculateRisk(int numberOfTriggers, int patientAge, String patientGender) throws IllegalArgumentException {
        if (numberOfTriggers < 0 || patientAge < 0 || patientGender == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        boolean isAMan = "Homme".equalsIgnoreCase(patientGender);
        boolean isAWoman = "Femme".equalsIgnoreCase(patientGender);

        if (!isAMan && !isAWoman) {
            throw new IllegalArgumentException("Invalid gender: " + patientGender);
        }

        if (numberOfTriggers == 0) {
            return Risk.NONE;
        }

        if (numberOfTriggers == 1 && patientAge == 30) {
            return Risk.BORDERLINE;
        }

        if (numberOfTriggers == 2 && patientAge > 30) {
            return Risk.BORDERLINE;
        }

        if ((isAMan && patientAge < 30 && numberOfTriggers >= 3 && numberOfTriggers < 5)
                || (isAWoman && patientAge < 30 && numberOfTriggers >= 4 && numberOfTriggers < 7)
                || (patientAge > 30 && numberOfTriggers >= 6 && numberOfTriggers < 8)) {
            return Risk.IN_DANGER;
        }

        if ((isAMan && patientAge < 30 && numberOfTriggers >= 5)
                || (isAWoman && patientAge < 30 && numberOfTriggers >= 7)
                || (patientAge > 30 && numberOfTriggers >= 8)) {
            return Risk.EARLY_ONSET;
        }

        return Risk.NONE;
    }


    public List<String> loadTriggersFromPatientNotes(List<Note> notes) {
        Triggers triggers = new Triggers();
        List<String> triggerList = triggers.triggerList();

        List<String> foundTriggers = new ArrayList<>();
        for (Note note : notes) {
            for (String trigger : triggerList) {
                if (note.getContent().contains(trigger)) {
                    foundTriggers.add(trigger);
                }
            }
        }

        return foundTriggers;
    }

     private Integer calculatePatientAge(String birthDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate birth = LocalDate.parse(birthDate, formatter);

        Period age = Period.between(birth, LocalDate.now());

        return age.getYears();
    }
}
