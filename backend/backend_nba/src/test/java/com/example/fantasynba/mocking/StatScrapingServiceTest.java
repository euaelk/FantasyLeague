package com.example.fantasynba.mocking;

import com.example.fantasynba.repository.PlayerRepository;
import com.example.fantasynba.repository.StatsRepository;
import com.example.fantasynba.scraping.Scraping;
import com.example.fantasynba.service.PlayerServiceImpl;
import com.example.fantasynba.service.TeamServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatScrapingServiceTest {


    private PlayerRepository playerRepository;
    private StatsRepository statsRepository;
    private Scraping scraper;

    @BeforeEach
    public void setup(){
        scraper = mock(Scraping.class);
        statsRepository = mock(StatsRepository.class);
        playerRepository = mock(PlayerRepository.class);
    }

    @Test
    public void createDocument() {

    }


}
