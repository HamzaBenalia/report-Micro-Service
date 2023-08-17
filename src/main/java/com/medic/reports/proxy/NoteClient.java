package com.medic.reports.proxy;

import com.medic.reports.model.Note;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "note", url = "http://localhost:9090")

public interface NoteClient {

    @GetMapping("/note/patient/{id}")
    List<Note> getNotesByPatientId(@PathVariable("id") String patientId);
}
