package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.UniversityDao;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.University;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UniversityServiceTest {

    @Autowired
    private UniversityService service;

    @MockBean
    private UniversityDao repository;

    private ObjectId id = new ObjectId();
    private University universityWithoutId = new University(null, "name", "country", "city", null);
    private University universityWithId = new University(id, "name", "country", "city", null);

    @Test
    @DisplayName("Add university, successful")
    public void addUniversitySuccessfulTest() {
        Mockito.when(repository.findByName("name")).thenReturn(Optional.empty());
        Mockito.when(repository.insert(universityWithoutId)).thenReturn(universityWithId);

        Optional<University> universityOptional = service.addUniversity(universityWithoutId);

        assertThat(universityOptional.isPresent()).isEqualTo(true);
        University newUniversity = universityOptional.get();
        assertThat(newUniversity.getId()).isEqualTo(id.toString());
        assertThat(newUniversity.getName()).isEqualTo("name");
        assertThat(newUniversity.getCountry()).isEqualTo("country");
        assertThat(newUniversity.getCity()).isEqualTo("city");
    }

    @Test
    @DisplayName("Add university, name conflict")
    public void addUniversityNameConflictTest() {
        Mockito.when(repository.findByName("name")).thenReturn(Optional.of(universityWithId));

        assertThat(service.addUniversity(universityWithoutId)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Get all universities")
    public void getAllUniversitiesTest() {
        University university2 = new University(new ObjectId(), "name2", "country2", "city2", null);
        University university3 = new University(new ObjectId(), "name3", "country3", "city3", null);
        Mockito.when(repository.findAll(1, 10)).thenReturn(List.of(universityWithId, university2, university3));

        assertThat(service.getAllUniversities(1, 10)).isEqualTo(List.of(universityWithId, university2, university3));
    }

    @Test
    @DisplayName("Get university by id, successful")
    public void getUniversityByIdSuccessfulTest() {
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(universityWithId));

        Optional<University> universityOptional = service.getUniversityById(id, 1, 10);

        assertThat(universityOptional.isPresent()).isEqualTo(true);
        University dbUniversity = universityOptional.get();
        assertThat(dbUniversity.getId()).isEqualTo(universityWithId.getId());
        assertThat(dbUniversity.getName()).isEqualTo(universityWithId.getName());
        assertThat(dbUniversity.getCountry()).isEqualTo(universityWithId.getCountry());
        assertThat(dbUniversity.getCity()).isEqualTo(universityWithId.getCity());
        assertThat(dbUniversity.getSubjects().size()).isEqualTo(universityWithId.getSubjects().size());
    }

    @Test
    @DisplayName("Get university by id, not exists")
    public void getUniversityByIdNotExistsTest() {
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(service.getUniversityById(id, 1, 10)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Delete university, successful")
    public void deleteUniversitySuccessfulTest() {
        Mockito.when(repository.deleteById(id)).thenReturn(1);

        assertThat(service.deleteUniversity(id)).isEqualTo(1);
    }

    @Test
    @DisplayName("Delete university, not exists")
    public void deleteUniversityNotExistsTest() {
        Mockito.when(repository.deleteById(id)).thenReturn(0);

        assertThat(service.deleteUniversity(id)).isEqualTo(0);
    }

    @Test
    @DisplayName("Get university by name")
    public void getUniversityByNameTest() {
        Mockito.when(repository.findByName("name")).thenReturn(Optional.of(universityWithId));

        assertThat(service.getUniversityByName("name")).isEqualTo(Optional.of(universityWithId));
    }

    @Test
    @DisplayName("Update university, successful")
    public void updateUniversitySuccessfulTest() {
        Mockito.when(repository.updateById(id, universityWithoutId)).thenReturn(Optional.of(universityWithId));

        assertThat(service.updateUniversityById(id, universityWithoutId)).isEqualTo(Optional.of(universityWithId));
    }

    @Test
    @DisplayName("Update university, not exists")
    public void updateUniversityNotExistsTest() {
        Mockito.when(repository.updateById(id, universityWithoutId)).thenReturn(Optional.empty());

        assertThat(service.updateUniversityById(id, universityWithoutId)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Remove subject, successful")
    public void removeSubjectSuccessfulTest() {
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        University university = universityWithId;
        university.addSubject(subject);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(university));

        assertThat(service.removeSubjectFromUniversity(id, subject)).isEqualTo(1);
    }

    @Test
    @DisplayName("Remove subject, not contains")
    public void removeSubjectNotContainsTest() {
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        University university = universityWithId;
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(university));

        assertThat(service.removeSubjectFromUniversity(id, subject)).isEqualTo(0);
    }

    @Test
    @DisplayName("Remove subject, not exists")
    public void removeSubjectNotExistsTest() {
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        University university = universityWithId;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(service.removeSubjectFromUniversity(id, subject)).isEqualTo(0);
    }

    @Test
    @DisplayName("Get universities by name regex")
    public void getUniversitiesByNameRegexTest() {
        University university2 = new University(new ObjectId(), "name2", "country2", "city2", null);
        University university3 = new University(new ObjectId(), "name3", "country3", "city3", null);
        Mockito.when(repository.findByNameRegex("name", 1, 10)).thenReturn(List.of(universityWithId, university2, university3));

        assertThat(service.getUniversitiesByNameRegex("name", 1, 10)).isEqualTo(List.of(universityWithId, university2, university3));
    }

    @Test
    @DisplayName("Subjects as pageable, not first page")
    public void subjectsToPageableNotFirstPageTest() {
        init();

        Optional<University> universityOptional = service.getUniversityById(id, 2, 1);

        assertThat(universityOptional.isPresent()).isEqualTo(true);
        University newUniversity = universityOptional.get();
        assertThat(newUniversity.getSubjects().size()).isEqualTo(1);
        assertThat(newUniversity.getSubjects().get(0).getCode()).isEqualTo("code2");
        assertThat(newUniversity.getSubjects().get(0).getName()).isEqualTo("name2");
    }

    @Test
    @DisplayName("Subjects as pageable, empty page")
    public void subjectsToPageableEmptyPageTest() {
        init();

        Optional<University> universityOptional = service.getUniversityById(id, 3, 1);

        assertThat(universityOptional.isPresent()).isEqualTo(true);
        assertThat(universityOptional.get().getSubjects().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Subjects as pageable, not full page")
    public void subjectsToPageableNotFullPageTest() {
        init();

        Optional<University> universityOptional = service.getUniversityById(id, 1, 10);

        assertThat(universityOptional.isPresent()).isEqualTo(true);
        List<Subject> subjects = universityOptional.get().getSubjects();
        assertThat(subjects.size()).isEqualTo(2);
        assertThat(subjects.get(0).getName()).isEqualTo("name1");
        assertThat(subjects.get(1).getName()).isEqualTo("name2");
    }

    private void init() {
        University university = universityWithId;
        Subject subject1 = new Subject(new ObjectId(), "code1", "name1", null);
        Subject subject2 = new Subject(new ObjectId(), "code2", "name2", null);
        university.addSubject(subject1);
        university.addSubject(subject2);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(university));
    }
}
