package com.example.fantasynba.scraping;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.PlayerStats;
import com.example.fantasynba.repository.StatsRepository;
import com.example.fantasynba.service.DateService;
import com.example.fantasynba.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@Service
@Transactional
@RequiredArgsConstructor
public class ScrapingImpl implements Scraping{

    private final PlayerService playerService;
    private final DateService dateService;
    private final StatsRepository statsRepository;

    static String[] boxScores = {
            "https://www.basketball-reference.com/leagues/NBA_2022_games-october.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-november.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-december.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-january.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-february.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-march.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-april.html"
    };


    @Override
    public List<Document> openFutureGames() {
        Document doc;
        List<Document> docs = new ArrayList<>();
        for (String s : boxScores) {
            try {
                doc = Jsoup.connect(s).get();
                docs.add(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return docs;
    }

    @Override
    public Elements getListOfBoxScores(Document doc){ // doc = month link e.g. october games
        Element body = doc.getElementById("schedule").select("tbody").first();
        return body != null ? body.select("tr") : null;
    }

    @Override
    public Map<String, String> getScoresByDate(Elements games)  { // get schedule for the month, pass in each game as a 'tr' element
        Map<String, String> game_dates = new HashMap<>();
        for (Element game : games) {
            String date = game.select("[data-stat=date_game]").text(); // formerly game_date
            Element box = game.select("[data-stat=box_score_text]").select("a").first();
            if (box == null) break;
            game_dates.put(box.attr("abs:href"), date);
        }
        return game_dates;
    }

    @Override
    @Async
    public void processVisitorHome(String link, String date) { // separate visitor/home box scores
        Document doc;
        Element awayScore = null;
        Element homeScore = null;
        try {
            doc = Jsoup.connect(link).newRequest().get(); // will run out of memory w/o creating a new request at each exec.
            String visiting_team = playerService.getTeamAbv().get(doc.getElementsByClass("scorebox").first()
                    .select("strong").get(0).select("a").text());

            String home_team = playerService.getTeamAbv().get(doc.getElementsByClass("scorebox").first()
                    .select("strong").get(1).select("a").text());

            awayScore = doc.getElementById(String.format("all_box-%s-game-basic", visiting_team));
            homeScore = doc.getElementById(String.format("all_box-%s-game-basic", home_team));

        } catch (IOException e) {
            e.printStackTrace();
        }
        processTeamData(awayScore, date);
        processTeamData(homeScore, date);
    }

    public void processTeamData(Element boxscore, String date) { // pass in player rows to start saving stats. Takes 29ms
        Elements players = boxscore.select("tbody").select("tr");
        players.stream().filter(p -> !p.hasAttr("class")).forEach(s -> scrapeStats(s, date));
    }

    public void scrapeStats(Element stat, String date)  {
        if (stat.hasAttr("[data-stat='reason']")) return;

        String name = stat.select("[data-stat=player]").text();
        LocalDate game_date = dateService.getDateFromString(date);
        Integer fieldGoal = stringConversion(stat.select("[data-stat=fg]").text());
        Integer fga = stringConversion(stat.select("[data-stat=fga]").text());
        Integer three = stringConversion(stat.select("[data-stat=fg3]").text());
        Integer pts = stringConversion(stat.select("[data-stat=pts]").text());
        Integer trb = stringConversion(stat.select("[data-stat=trb]").text());
        Integer ast = stringConversion(stat.select("[data-stat=ast]").text());
        Integer stl = stringConversion(stat.select("[data-stat=stl]").text());
        Integer blk = stringConversion(stat.select("[data-stat=blk]").text());
        Integer tov = stringConversion(stat.select("[data-stat=tov]").text());

        Player p = playerService.getPlayer(name);
        if (p == null) return;
        PlayerStats s = statsBuilder(p, game_date, fieldGoal, fga, three, pts, trb, ast, stl, blk, tov);

        saveStats(s);

    }

    public void saveStats(PlayerStats p){
        statsRepository.save(p);
        p.getPlayer().recordPlayerStat(p);
    }

    public PlayerStats statsBuilder(Player player, LocalDate date, Integer fg, Integer fga, Integer threeP,
                                     Integer pts, Integer trb, Integer ast, Integer stl, Integer blk, Integer tov){
        return PlayerStats.builder()
                .player(player)
                .playerName(player.getName())
                .date(date)
                .fg(fg)
                .fga(fga)
                .threeP(threeP)
                .pts(pts)
                .trb(trb)
                .ast(ast)
                .blk(blk)
                .stl(stl)
                .tov(tov)
                .build();

    }

    @Override
    public Integer stringConversion(String s) throws NumberFormatException {
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    @Override
    public String[] getBoxScores() {
        return boxScores;
    }

}
