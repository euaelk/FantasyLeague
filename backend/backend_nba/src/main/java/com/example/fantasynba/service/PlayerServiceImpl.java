package com.example.fantasynba.service;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.PlayerRepository;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@Transactional
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamService teamService;
    private Map<String, String> teamLinks;

    private static Map<String, String> teamAbv = new HashMap<>();

    static {
        teamAbv.put("Atlanta Hawks", "ATL");
        teamAbv.put("Boston Celtics", "BOS");
        teamAbv.put("Brooklyn Nets", "BRK");
        teamAbv.put("Charlotte Hornets", "CHO");
        teamAbv.put("Chicago Bulls", "CHI");
        teamAbv.put("Cleveland Cavaliers", "CLE");
        teamAbv.put("Dallas Mavericks", "DAL");
        teamAbv.put("Denver Nuggets", "DEN");
        teamAbv.put("Detroit Pistons", "DET");
        teamAbv.put("Golden State Warriors", "GSW");
        teamAbv.put("Houston Rockets", "HOU");
        teamAbv.put("Indiana Pacers", "IND");
        teamAbv.put("Los Angeles Lakers", "LAL");
        teamAbv.put("Los Angeles Clippers", "LAC");
        teamAbv.put("Memphis Grizzlies", "MEM");
        teamAbv.put("Miami Heat", "MIA");
        teamAbv.put("Milwaukee Bucks", "MIL");
        teamAbv.put("Minnesota Timberwolves", "MIN");
        teamAbv.put("New Orleans Pelicans", "NOP");
        teamAbv.put("New York Knicks", "NYK");
        teamAbv.put("Oklahoma City Thunder", "OKC");
        teamAbv.put("Orlando Magic", "ORL");
        teamAbv.put("Philadelphia 76ers", "PHI");
        teamAbv.put("Phoenix Suns", "PHO");
        teamAbv.put("Portland Trail Blazers", "POR");
        teamAbv.put("Sacramento Kings", "SAC");
        teamAbv.put("San Antonio Spurs", "SAS");
        teamAbv.put("Toronto Raptors", "TOR");
        teamAbv.put("Utah Jazz", "UTA");
        teamAbv.put("Washington Wizards", "WAS");
    }


    @Override
    @Async
    public void fetchActivePlayers() {
        teamLinks = new HashMap<>();
        final String rosterSite = "https://www.basketball-reference.com/teams/%s/2022.html";
        teamAbv.forEach((name, abv) -> {
            teamLinks.put(name, String.format(rosterSite, abv));
        });
        fetchPlayers();
    }

    @Override
    public void fetchPlayers() {
        List<CompletableFuture<Document>> asyncRosters = new ArrayList<>();
        teamLinks.forEach((name, rosterLink) ->
                asyncRosters.add(CompletableFuture.supplyAsync(() -> returnFutureUrl(rosterLink, name))));
        CompletableFuture.allOf(asyncRosters.toArray(new CompletableFuture[0])).join();
    }

    private Document returnFutureUrl(String url, String team) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            processRosterPlayerData(doc, team);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private void processRosterPlayerData(Document doc, String team){
        Element body = doc.getElementById("roster").select("tbody").first();
        for (Element element : body.select("tr")) {
            Iterator<Element> playerInfo = element.getAllElements().iterator();
            fillPlayerInfo(playerInfo.next(), team);
        }
    }

    @Override
    public void fillPlayerInfo(Element e, String team) {
        String name = e.select("[data-stat=player]").text();
        String position = e.select("[data-stat=pos]").text();
        String height = e.select("[data-stat=height]").text();
        String weight = e.select("[data-stat=weight]").text();
        Integer lbs = returnInt(weight);
        String dob = e.select("[data-stat=birth_date]").text();
        String college = e.select("[data-stat=college]").text();
        Team player_team = teamService.findTeam(team);

        Player playerExists = findPlayer(name);
        Player p = buildPlayer(name,position,height,lbs,dob,college,player_team);
        if (playerExists == null) { savePlayerDB(p); }
        else if (!p.equals(playerExists)) { savePlayerDB(p); }

    }

    @Override
    public Player buildPlayer(String name, String position, String height, Integer lbs, String dob, String college, Team team) {
       return Player.builder()
                .name(name)
                .position(position)
                .height(height)
                .weight(lbs)
                .dob(dob)
                .college(college)
                .team(team)
                .build();
    }

    @Override
    public void savePlayerDB(Player p){
        playerRepository.save(p);
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Player findPlayer(String name) {
        return playerRepository.findByName(name);
    }

    @Override
    public Integer returnInt(String s) {
        int weight;
        if (s.isEmpty())
            return -1;
        try {
            weight = Integer.parseInt(s);
        } catch (NumberFormatException err) {
            throw new RuntimeException(err);
        }
        return weight;
    }

    @Override
    public Map<String, String> getTeamAbv() {
        return teamAbv;
    }
}
