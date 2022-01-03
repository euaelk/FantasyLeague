package com.example.fantasynba.service;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.PlayerRepository;
import com.example.fantasynba.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private Map<String, String> teamLinks;

    public static Map<String, String> teamAbrev;

    static {
        teamAbrev = new HashMap<>();
        teamAbrev.put("Atlanta Hawks", "ATL");
        teamAbrev.put("Boston Celtics", "BOS");
        teamAbrev.put("Brooklyn Nets", "BRK");
        teamAbrev.put("Charlotte Hornets", "CHO");
        teamAbrev.put("Chicago Bulls", "CHI");
        teamAbrev.put("Cleveland Cavaliers", "CLE");
        teamAbrev.put("Dallas Mavericks", "DAL");
        teamAbrev.put("Denver Nuggets", "DEN");
        teamAbrev.put("Detroit Pistons", "DET");
        teamAbrev.put("Golden State Warriors", "GSW");
        teamAbrev.put("Houston Rockets", "HOU");
        teamAbrev.put("Indiana Pacers", "IND");
        teamAbrev.put("Los Angeles Lakers", "LAL");
        teamAbrev.put("Los Angeles Clippers", "LAC");
        teamAbrev.put("Memphis Grizzlies", "MEM");
        teamAbrev.put("Miami Heat", "MIA");
        teamAbrev.put("Milwaukee Bucks", "MIL");
        teamAbrev.put("Minnesota Timberwolves", "MIN");
        teamAbrev.put("New Orleans Pelicans", "NOP");
        teamAbrev.put("New York Knicks", "NYK");
        teamAbrev.put("Oklahoma City Thunder", "OKC");
        teamAbrev.put("Orlando Magic", "ORL");
        teamAbrev.put("Philadelphia 76ers", "PHI");
        teamAbrev.put("Phoenix Suns", "PHO");
        teamAbrev.put("Portland Trail Blazers", "POR");
        teamAbrev.put("Sacramento Kings", "SAC");
        teamAbrev.put("San Antonio Spurs", "SAS");
        teamAbrev.put("Toronto Raptors", "TOR");
        teamAbrev.put("Utah Jazz", "UTA");
        teamAbrev.put("Washington Wizards", "WAS");
    }


    @Override
    public void fetchActivePlayers() {
        teamLinks = new HashMap<>();
        String pre = "https://www.basketball-reference.com/teams/";
        String year = "/2022.html";

        teamAbrev.forEach((name, abrev) -> { // ex. https://www.basketball-reference.com/teams/ + BOS + /2022.html
            StringJoiner s = new StringJoiner(abrev);
            s.add(pre);
            s.add(year);
            teamLinks.put(name, s.toString());
        });
        try {
            fetchPlayers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fetchPlayers() throws IOException {

        teamLinks.forEach((name, link) -> {

            Document doc = null;
            try {
                doc = Jsoup.connect(link).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element table = doc.getElementById("roster");
            Element table_body = table.select("tbody").first();

            Iterator<Element> tr_iterator = table_body.select("tr").iterator();
            while (tr_iterator.hasNext()) {
                Iterator<Element> playerInfo = tr_iterator.next().getAllElements().iterator();
                savePlayer(name, playerInfo.next());
            }
        });

    }

    @Override
    @Transactional
    public void savePlayer(String team, Element e) {

        String name = e.select("[data-stat=player]").text();
        String position = e.select("[data-stat=pos]").text();
        String height = e.select("[data-stat=height]").text();
        String weight = e.select("[data-stat=weight]").text();
        Integer lbs = returnInt(weight);
        String dob = e.select("[data-stat=birth_date]").text();
        String college = e.select("[data-stat=college]").text();
        Team player_team = teamRepository.findByName(team);

        Player p = Player.builder()
                .name(name)
                .position(position)
                .height(height)
                .weight(lbs)
                .dob(dob)
                .college(college)
                .team(player_team)
                .build();


        playerRepository.save(p);
        teamRepository.findByName(team).addPlayer(p);
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
        Integer weight;
        if (s.isEmpty() || s == null)
            return -1;
        try {
            weight = Integer.valueOf(s);
        } catch (NumberFormatException err) {
            throw new RuntimeException(err);
        }
        return weight;
    }
}
