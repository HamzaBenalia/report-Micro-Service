package com.medic.reports.testService;

import com.medic.reports.exception.PatientNotFoundException;
import com.medic.reports.model.Note;
import com.medic.reports.model.Patient;
import com.medic.reports.model.Report;
import com.medic.reports.proxy.NoteClient;
import com.medic.reports.proxy.PatientClient;
import com.medic.reports.services.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class testReportService {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private Report report;

    @Mock
    private PatientClient patientClient;

    @Mock
    private NoteClient noteClient;


    @Captor
    ArgumentCaptor<Report> reportArgumentCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);  // Initialize mocks before each test
    }


    @Test
    public void testLoadTriggersFromPatientNotes() {
        // Préparation des données de test
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
        Patient mockPatient = new Patient("2", "Doe", "John", "01/01/1990", "Homme", "City", "123456789");
        Note mockNote = new Note("1", LocalDate.now(), mockPatient.getId(), "Content");
        List<Note> notes = Collections.singletonList(mockNote);

        when(patientClient.getPatientById("2")).thenReturn(mockPatient);
        when(noteClient.getNotesByPatientId("2")).thenReturn(notes);

        Report report = reportService.generateReports(mockPatient.getId());

        assertNotNull(report);
        assertEquals("1", report.getPatientId());
        // Continuez avec d'autres assertions pour vérifier les autres champs du rapport
    }



    @Test
    public void testGenerateReports_ValidPatientId() throws  PatientNotFoundException {
        String validPatientId = "1";
        Patient patient = new Patient(validPatientId, "benalia", "Hamza", "16/02/1995", "Homme", "Toulouse", "0766764619");
        Note mockNote = new Note("1", LocalDate.now(), validPatientId, "Some content");

        when(patientClient.getPatientById("1")).thenReturn(patient);
        when(noteClient.getNotesByPatientId(validPatientId)).thenReturn(Arrays.asList(mockNote));

        Report report = reportService.generateReports(validPatientId);

        assertNotNull(report);
        assertEquals(validPatientId, report.getPatientId());
    }
}