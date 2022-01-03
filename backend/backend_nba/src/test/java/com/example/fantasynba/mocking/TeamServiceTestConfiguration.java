package com.example.fantasynba.mocking;

import com.example.fantasynba.service.TeamServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TeamServiceTestConfiguration {

    @Bean
    @Primary
    public TeamServiceImpl teamService(){
        return Mockito.mock(TeamServiceImpl.class);
    }
}
