package com.example.fantasynba.mocking;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import javax.persistence.EntityManager;
import java.util.logging.Level;
import java.util.logging.LogManager;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;


@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@Transactional(propagation = NOT_SUPPORTED)
public class ManualTransactionIntegrationTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    @Container
    private static final PostgreSQLContainer<?> pg = initPostgres();

    private TransactionTemplate transactionTemplate; // simplifies programmatic transaction demarcation and transaction exception handling

    @BeforeEach
    void setUp(){
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    static {
        // Postgres JDBC driver uses JUL; disable it to avoid annoying, irrelevant, stderr logs during connection testing
        LogManager.getLogManager().getLogger("").setLevel(Level.OFF);
    }

    private static PostgreSQLContainer<?> initPostgres() {
        PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:11.1")
                .withDatabaseName("nba")
                .withUsername("test")
                .withPassword("test");
        pg.setPortBindings(singletonList("54320:5432"));

        return pg;
    }

    @Test
    void givenAPlayer_WhenNotDuplicate_ThenShouldCommit() { // execute can run any block of code inside a transaction
        Long id = transactionTemplate.execute(status -> {
            Player player = new Player();
            player.setName("Jim McGruder");
            player.setCollege("UNC");
            player.setPosition("SF");
            player.setHeight("6-6");
            player.setDob("Nov 5 1997");
            player.setWeight(180);
            entityManager.persist(player);

            return player.getId();
        });

        Player player = entityManager.find(Player.class, id);
        assertThat(player).isNotNull();
    }

    @Test
    void givenTwoPlayers_WhenInfoIdentical_ThenShouldRollback(){
        try {
            transactionTemplate.execute(status -> {
                Team team = new Team("LAL");
                Player first = new Player();
                first.setName("Jim McGruder");
                first.setCollege("UNC");
                first.setPosition("SF");
                first.setHeight("6-6");
                first.setDob("Nov 5 1997");
                first.setWeight(180);
                first.setTeam(team);

                Player second = new Player();
                second.setName("Jim McGruder");
                second.setCollege("UNC");
                second.setPosition("SF");
                second.setHeight("6-6");
                second.setDob("Nov 5 1997");
                second.setWeight(180);
                second.setTeam(team);

                entityManager.persist(first);
                entityManager.persist(second);

                return first.getId();
            });
        } catch(Exception ignored){}
        assertThat(entityManager.createQuery("select p from Player p").getResultList()).isEmpty();
    }


}
