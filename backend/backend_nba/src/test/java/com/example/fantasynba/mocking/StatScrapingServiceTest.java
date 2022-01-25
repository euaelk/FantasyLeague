package com.example.fantasynba.mocking;

import com.example.fantasynba.repository.PlayerRepository;
import com.example.fantasynba.repository.StatsRepository;
import com.example.fantasynba.scraping.Scraping;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void testDocument() {

    }


}
