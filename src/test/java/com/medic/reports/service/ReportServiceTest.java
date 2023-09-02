package com.medic.reports.service;

import com.medic.reports.enums.Risk;
import com.medic.reports.exception.PatientNotFoundException;
import com.medic.reports.model.Note;
import com.medic.reports.model.Patient;
import com.medic.reports.model.Report;
import com.medic.reports.proxy.NoteClient;
import com.medic.reports.proxy.PatientClient;
import com.medic.reports.services.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private PatientClient patientClient;

    @Mock
    private NoteClient noteClient;


    @Test
    public void testLoadTriggersFromPatientNotes() {
        LocalDate now = LocalDate.now();
        List<Note> noteList = Arrays.asList(
                new Note("1", now, "1", "Hemoglobin A1C, Microalbumin"),
                new Note("2", now, "2", "Smoker, Reaction")
        );

        List<String> actualTriggers = reportService.loadTriggersFromPatientNotes(noteList);
        List<String> expectedTriggers = Arrays.asList("Hemoglobin A1C", "Microalbumin", "Smoker", "Reaction");
        assertEquals(expectedTriggers, actualTriggers);
    }

    @Test
    public void testGenerateReports_PatientIdIsNull() {
        assertThrows(PatientNotFoundException.class, () -> reportService.generateReports(null));
    }

    @Test
    public void testGenerateReports_PatientIdIsEmpty() {
        assertThrows(PatientNotFoundException.class, () -> reportService.generateReports(""));
    }

    @Test
    public void testGenerateReports_Success() throws PatientNotFoundException {
        Patient patient = new Patient("2", "Doe", "John", "01/01/1990", "Homme", "City", "123456789");
        Note mockNote = new Note("1", LocalDate.now(), patient.getId(), "Content");
        List<Note> notes = Collections.singletonList(mockNote);

        when(patientClient.getPatientById("2")).thenReturn(patient);
        when(noteClient.getNotesByPatientId("2")).thenReturn(notes);
        Report report = reportService.generateReports(patient.getId());

        assertNotNull(report);
        assertEquals("2", report.getPatientId());
    }

    @Test
    public void test_mock() {
        Patient mockPatient = new Patient("2", "Doe", "John", "01/01/1990", "Homme", "City", "123456789");
        when(patientClient.getPatientById("2")).thenReturn(mockPatient);

        Patient directMockCall = patientClient.getPatientById("2");
        assertNotNull(directMockCall);
    }

    @Test
    public void testNoTriggersReturnsNone() {
        assertEquals(Risk.NONE, reportService.calculateRisk(0, 25, "Homme"));
    }

    @Test
    public void testSingleTriggerAge30ReturnsBorderline() {
        assertEquals(Risk.BORDERLINE, reportService.calculateRisk(1, 30, "Homme"));
    }

    @Test
    public void testTwoTriggersAgeOver30ReturnsBorderline() {
        assertEquals(Risk.BORDERLINE, reportService.calculateRisk(2, 35, "Femme"));
    }

    @Test
    public void testMenUnder30With3TriggersReturnsInDanger() {
        assertEquals(Risk.IN_DANGER, reportService.calculateRisk(3, 25, "Homme"));
    }

    @Test
    public void testWomenUnder30With4TriggersReturnsInDanger() {
        assertEquals(Risk.IN_DANGER, reportService.calculateRisk(4, 25, "Femme"));
    }

    @Test
    public void testAgeOver30With6TriggersReturnsInDanger() {
        assertEquals(Risk.IN_DANGER, reportService.calculateRisk(6, 35, "Homme"));
    }

    @Test
    public void testPatientNotFoundById() {
        when(patientClient.getPatientById("123")).thenReturn(null);
        try {
            reportService.generateReports("123");
            fail("Expected PatientNotFoundException to be thrown");
        } catch (PatientNotFoundException e) {
            assertEquals("Patient with given ID was not found", e.getMessage());
        }
    }

    @Test
    public void testNegativeNumberOfTriggersThrowsException() {
        try {
            reportService.calculateRisk(-1, 25, "Homme");
            fail("Expected IllegalArgumentException with message 'Invalid input' to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input", e.getMessage());
        }
    }

    @Test
    public void testNegativePatientAgeThrowsException() {
        try {
            reportService.calculateRisk(1, -5, "Homme");
            fail("Expected IllegalArgumentException with message 'Invalid input' to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input", e.getMessage());
        }
    }

    @Test
    public void testNullPatientGenderThrowsException() {
        try {
            reportService.calculateRisk(1, 25, null);
            fail("Expected IllegalArgumentException with message 'Invalid input' to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input", e.getMessage());
        }
    }

    @Test
    public void testInvalidGenderThrowsException() {
        String invalidGender = "InvalidGender";
        try {
            reportService.calculateRisk(1, 25, invalidGender);
            fail("Expected IllegalArgumentException with message 'Invalid gender: " + invalidGender + "' to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid gender: " + invalidGender, e.getMessage());
        }
    }

    @Test
    public void testEarlyOnsetForYoungMan() {
        Risk risk = reportService.calculateRisk(5, 25, "Homme");
        assertEquals(Risk.EARLY_ONSET, risk);

        risk = reportService.calculateRisk(6, 25, "Homme");
        assertEquals(Risk.EARLY_ONSET, risk);
    }

    @Test
    public void testEarlyOnsetForYoungWoman() {
        Risk risk = reportService.calculateRisk(7, 25, "Femme");
        assertEquals(Risk.EARLY_ONSET, risk);

        risk = reportService.calculateRisk(8, 25, "Femme");
        assertEquals(Risk.EARLY_ONSET, risk);
    }

    @Test
    public void testEarlyOnsetForElderly() {
        Risk risk = reportService.calculateRisk(8, 35, "Homme");
        assertEquals(Risk.EARLY_ONSET, risk);

        risk = reportService.calculateRisk(8, 35, "Femme");
        assertEquals(Risk.EARLY_ONSET, risk);

        risk = reportService.calculateRisk(9, 35, "Homme");
        assertEquals(Risk.EARLY_ONSET, risk);
    }
}