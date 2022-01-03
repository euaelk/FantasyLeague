package com.example.fantasynba.mocking;

import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.TeamRepository;
import com.example.fantasynba.service.TeamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TeamServiceUnitTest {

    @MockBean(TeamRepository.class)
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    private Team team;

    @Test
    public void contextLoads() throws Exception {
        team = new Team();

        given(this.teamRepository.findByName("Seattle Sonics")).willReturn(team);

        assertTrue(teamService.findTeam("Seattle Sonics").getName() != "Seattle Sonics");
    }

}
