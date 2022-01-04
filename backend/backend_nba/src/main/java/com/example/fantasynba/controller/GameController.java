package com.example.fantasynba.controller;


import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.GameRepository;
import com.example.fantasynba.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.time.LocalDate;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8090")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class GameController {

    private final GameRepository gameRepository;
    private final GameScraper gameScraper;
    private final DateService dateService;
    private final PlayerService playerService;
    private final TeamService teamService;

    public String url = "https://www.basketball-reference.com/leagues/NBA_2022_games.html";

    @GetMapping("")
    public ResponseEntity<List<Game>> getSeasonSchedule() throws IOException {
        gameScraper.fetchGameData(url);
        teamService.nbaStandings();
        playerService.fetchActivePlayers();
        List<Game> retrievedGames = gameScraper.getAllGames();
        return new ResponseEntity<>(retrievedGames, HttpStatus.OK);
    }

    @GetMapping("/games")
    @ResponseBody
    public ResponseEntity<List<Game>> getAllGames(){
        List<Game> games = gameScraper.getAllGames();
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    @GetMapping("/games/date")
    public ResponseEntity<List<Game>> getTodaysGames(){
        LocalDate ld = dateService.getDateFromString("Wed, Dec 1, 2021");
        List<Game> todayGames = gameRepository.findByDate(ld);
        return new ResponseEntity<>(todayGames, HttpStatus.OK);
    }

    @GetMapping("/team/{team}")
    public ResponseEntity<Team> getTeam(@PathVariable String team){
        return new ResponseEntity<>(teamService.findTeam(team), HttpStatus.OK);
    }

    @GetMapping("/player/{name}")
    public ResponseEntity<Player> getPlayer(@PathVariable String name){
        return new ResponseEntity<>(playerService.findPlayer(name), HttpStatus.OK);
    }

}
