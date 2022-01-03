package com.example.fantasynba.controller;


import com.example.fantasynba.domain.Game;
import com.example.fantasynba.repository.GameRepository;
import com.example.fantasynba.service.DateService;
import com.example.fantasynba.service.GameService;
import com.example.fantasynba.service.GameScraper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8090")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {

    private final GameRepository gameRepository;
    private final GameService gameService;
    private final GameScraper gameScraper;
    private final DateService dateService;

    public String url = "https://www.basketball-reference.com/leagues/NBA_2022_games.html";

    @GetMapping("/data")
    public ResponseEntity<List<Game>> getSeasonSchedule(){
        gameScraper.fetchGameData(url);
        List<Game> retrievedGames = gameRepository.findAll();
        return new ResponseEntity<>(retrievedGames, HttpStatus.OK);
    }

    @GetMapping("/games")
    @ResponseBody
    public ResponseEntity<List<Game>> getAllGames(){
        List<Game> games = gameRepository.findAll();
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    @GetMapping("/games/date")
    public ResponseEntity<List<Game>> getTodaysGames(){
        LocalDate ld = dateService.getDateFromString("Wed, Dec 1, 2021");
        List<Game> todayGames = gameRepository.findByDate(ld);
        return new ResponseEntity<>(todayGames, HttpStatus.OK);
    }

    @GetMapping("/games/{team}")
    public ResponseEntity<List<Game>> getTeamGames(@PathVariable String team){
        List<Game> teamGames = gameRepository.findByTeam(team);
        return new ResponseEntity<>(teamGames, HttpStatus.OK);
    }

}
