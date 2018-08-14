package ru.saidgadjiev.aboutme.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.saidgadjiev.aboutme.utils.JsonUtils;
import ru.saidgadjiev.aboutme.configuration.TestConfiguration;
import ru.saidgadjiev.aboutme.domain.AboutMe;
import ru.saidgadjiev.aboutme.domain.Role;
import ru.saidgadjiev.aboutme.domain.Skill;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by said on 09.08.2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@AutoConfigureMockMvc
public class AboutMeControllerIntegrationTest {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void before() throws SQLException {
        try (Session session = sessionManager.createSession()) {
            session.clearTables(Skill.class, AboutMe.class);
            session.statementBuilder().createQuery("ALTER TABLE skill ALTER COLUMN id RESTART WITH 1").executeUpdate();
        }
    }

    @Test
    public void getAboutMe() throws Exception {
        AboutMe aboutMe = createAboutMe();

        createSkill(aboutMe);

        mockMvc
                .perform(get("/api/aboutMe").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fio", is("Гаджиев Саид Алиевич")))
                .andExpect(jsonPath("$.placeOfResidence", is("Москва")))
                .andExpect(jsonPath("$.educationLevel", is("Бакалавр")))
                .andExpect(jsonPath("$.post", is("Java разработчик")))
                .andExpect(jsonPath("$.skills", hasSize(1)))
                .andExpect(jsonPath("$.skills[0].id", is(1)))
                .andExpect(jsonPath("$.skills[0].name", is("Java")))
                .andExpect(jsonPath("$.skills[0].percentage", is(90)));
    }

    @Test
    public void update() throws Exception {
        createAboutMe();

        mockMvc.perform(post("/api/aboutMe/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"post\":\"Test2\",\"placeOfResidence\":\"Test1\"}")
                .with(user("test").authorities(new SimpleGrantedAuthority(Role.ROLE_ADMIN)))
        )
                .andExpect(status().isOk());

        try (Session session = sessionManager.createSession()) {
            AboutMe result = session.statementBuilder().createSelectStatement(AboutMe.class).uniqueResult();
            AboutMe expect = getTestAboutMe();

            expect.setPlaceOfResidence("Test1");
            expect.setPost("Test2");

            Assert.assertEquals(JsonUtils.toJson(expect), JsonUtils.toJson(result));
        }
    }

    @Test
    public void updateWithUnauthorized() throws Exception {
        mockMvc.perform(post("/api/aboutMe/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonUtils.toJson(getTestAboutMe()))
        )
                .andExpect(status().isUnauthorized());
    }

    private void createSkill(AboutMe aboutMe) throws SQLException {
        try (Session session = sessionManager.createSession()) {
            Skill skill = getTestSkill();

            skill.setAboutMe(aboutMe);

            session.create(skill);
            aboutMe.getSkills().add(skill);
        }
    }

    private AboutMe createAboutMe() throws SQLException {
        try (Session session = sessionManager.createSession()) {
            AboutMe aboutMe = getTestAboutMe();

            session.create(aboutMe);

            return aboutMe;
        }
    }

    private AboutMe getTestAboutMe() {
        AboutMe aboutMe = new AboutMe();

        aboutMe.setId(1);
        aboutMe.setFio("Гаджиев Саид Алиевич");
        aboutMe.setDateOfBirth(LocalDate.of(1995, 7, 1));
        aboutMe.setPlaceOfResidence("Москва");
        aboutMe.setPost("Java разработчик");
        aboutMe.setEducationLevel("Бакалавр");

        return aboutMe;
    }

    private Skill getTestSkill() {
        Skill skill = new Skill();

        skill.setName("Java");
        skill.setPercentage(90);

        return skill;
    }
}