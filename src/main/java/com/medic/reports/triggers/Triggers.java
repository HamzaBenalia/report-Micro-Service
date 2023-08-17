package com.medic.reports.triggers;

import java.util.Arrays;
import java.util.List;

public class Triggers {

    static String[] triggers = new String[]{
            "Hemoglobin A1C", "Microalbumin", "Height", "Weight", "Smoker",
            "Abnormal", "Cholesterol", "Vertigo", "Relapse", "Reaction", "Antibodies"
    };

    public List<String> triggerList() {
        return Arrays.asList(triggers);
    }

}
