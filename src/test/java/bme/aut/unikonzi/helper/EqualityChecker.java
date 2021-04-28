package bme.aut.unikonzi.helper;

import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.University;

import static org.assertj.core.api.Assertions.assertThat;

public class EqualityChecker {
    public static void areUniversitiesEquals(University actual, University expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getCountry()).isEqualTo(expected.getCountry());
        assertThat(actual.getCity()).isEqualTo(expected.getCity());
        assertThat(actual.getSubjects().size()).isEqualTo(expected.getSubjects().size());
    }

    public static void areSubjectsEquals(Subject actual, Subject expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getComments().size()).isEqualTo(expected.getComments().size());
        assertThat(actual.getTutors().size()).isEqualTo(expected.getTutors().size());
        assertThat(actual.getPupils().size()).isEqualTo(expected.getPupils().size());
    }
}
