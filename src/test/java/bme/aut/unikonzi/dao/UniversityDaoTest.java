package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.dao.impl.MongoUniversityDao;
import bme.aut.unikonzi.model.University;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UniversityDaoTest {

    @Autowired
    private MongoUniversityDao universityDao;

    @BeforeEach
    private void init() {
        universityDao.findAll(1, 10).forEach(
                u -> universityDao.deleteById( new ObjectId(u.getId()) )
        );
    }

    @DisplayName("Update by id")
    @Test
    public void updateTest() {
        ObjectId id = new ObjectId();
        University uni = new University(id, "university", "country", "city", Collections.emptyList());
        assertThat(universityDao.findAll(1, 10).size()).isEqualTo(0);
        uni = universityDao.insert(uni);
        assertThat(universityDao.findAll(1, 10).size()).isEqualTo(1);
        uni = new University(null, "new uni", "new country", "new city", Collections.emptyList());
        universityDao.updateById(id, uni);
        University dbUni = universityDao.findAll(1, 10).get(0);

        assertThat(dbUni.getId()).isEqualTo(id.toString());
        assertThat(dbUni.getName()).isEqualTo(uni.getName());
        assertThat(dbUni.getCountry()).isEqualTo(uni.getCountry());
        assertThat(dbUni.getCity()).isEqualTo(uni.getCity());
        assertThat(dbUni.getSubjects()).isEqualTo(uni.getSubjects());
    }

    @DisplayName("Find by name")
    @Test
    public void findByNameTest() {
        ObjectId id = new ObjectId();
        University uni = new University(id, "university", "country", "city", Collections.emptyList());
        uni = universityDao.insert(uni);
        assertThat(universityDao.findByName("other")).isEqualTo(Optional.empty());
        University dbUni = universityDao.findByName("university").get();
        assertThat(dbUni.getId()).isEqualTo(uni.getId());
        assertThat(dbUni.getName()).isEqualTo(uni.getName());
        assertThat(dbUni.getCountry()).isEqualTo(uni.getCountry());
        assertThat(dbUni.getCity()).isEqualTo(uni.getCity());
        assertThat(dbUni.getSubjects()).isEqualTo(uni.getSubjects());
    }

    @DisplayName("Find all by name like")
    @Test
    public void findAllByName() {
        University uni = new University(new ObjectId(), "university", "country", "city", Collections.emptyList());
        University uni2 = new University(new ObjectId(), "university2", "country", "city", Collections.emptyList());
        University uni3 = new University(new ObjectId(), "a new name", "country", "city", Collections.emptyList());
        universityDao.insert(uni);
        universityDao.insert(uni2);
        universityDao.insert(uni3);
        assertThat(universityDao.findAll(1, 10).size()).isEqualTo(3);
        assertThat(universityDao.findByNameRegex("name", 1, 10).size()).isEqualTo(1);
        assertThat(universityDao.findByNameRegex("uni", 1, 10).size()).isEqualTo(2);
    }
}
