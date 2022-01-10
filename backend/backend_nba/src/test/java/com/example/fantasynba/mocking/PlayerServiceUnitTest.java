package com.example.fantasynba.mocking;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.repository.PlayerRepository;
import com.example.fantasynba.service.PlayerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlayerServiceUnitTest {

    @MockBean(PlayerRepository.class)
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    private Player player;

    @Test
    public void duplicatePlayers() throws Exception {

    }

}
