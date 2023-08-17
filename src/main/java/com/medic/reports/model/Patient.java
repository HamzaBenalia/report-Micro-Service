package com.medic.reports.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {


    private String id;

    private String nom;

    private String prenom;

    private String dateDeNaissance;


    private String genre;

    private String adressePostale;


    private String numeroDeTelephone;

}
