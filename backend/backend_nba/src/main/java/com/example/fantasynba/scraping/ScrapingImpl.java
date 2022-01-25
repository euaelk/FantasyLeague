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

import org.springframework.hateoas.mediatype.alps.Doc;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
@Transactional
@RequiredArgsConstructor
public class ScrapingImpl implements Scraping{

    private final PlayerService playerService;
    private final DateService dateService;
    private final StatsRepository statsRepository;
    private String visiting_team, home_team;
    private static String date;
    private static List<Element> players;

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
    public void getBoxScoreData() {
        Arrays.stream(this.getBoxScores()).forEach(link -> openFutureGames(link));
    }

    @Override
    @Async("exec1")
    public CompletableFuture<String> openFutureGames(String url) { // connect to each month
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            processBoxScore(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(url).completable();
    }

    private void processBoxScore(Document doc)  { // get schedule for the month, pass in each game as a 'tr' element
        Element body = doc.getElementById("schedule").select("tbody").first();
        Elements games = body.select("tr");
        games.stream().filter(p -> !p.hasAttr(".thead")).forEach(e -> futureScoreLink(e));
    }

    private void futureScoreLink(Element game) { // get date, visitor/home teams of each game
        date = game.select("[data-stat=date_game]").text(); // formerly game_date
        visiting_team = playerService.getTeamAbv().get(game.select("[data-stat=visitor_team_name]").text());
        home_team = playerService.getTeamAbv().get(game.select("[data-stat=home_team_name]").text());
        Element box = game.select("[data-stat=box_score_text]").select("a").first();
        if (box != null) processVisitorHome(box.attr("abs:href"), date);
        else return;
    }


    private void processVisitorHome(String link, String date) { // separate visitor/home box scores
        Document doc;
        try {
            doc = Jsoup.connect(link).get();
            Element away_team_box_score = doc.getElementById(String.format("all_box-%s-game-basic", visiting_team));
            Element home_team_box_score = doc.getElementById(String.format("all_box-%s-game-basic", home_team));
            if (away_team_box_score != null) {processTeamData(away_team_box_score, date);}
            if (home_team_box_score != null) {processTeamData(home_team_box_score, date);}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processTeamData(Element boxscore, String date) { // pass in player rows to start saving stats. Takes 29ms
        Elements players = boxscore.select("tbody").select("tr");
        players.stream().filter(p -> !p.hasAttr("class")).forEach(s -> scrapeStats(s, date));
    }

    public void scrapeStats(Element stat, String date)  {
        if (stat.hasAttr("[data-stat='reason']")) return;

        String name = stat.select("[data-stat=player]").text();
        LocalDate game_date = dateService.getDateFromString(date);
        Integer fieldGoal = stringConversion(stat.select("[data-stat=fg]").text());
        Integer fgA = stringConversion(stat.select("[data-stat=fga]").text());
        Integer three = stringConversion(stat.select("[data-stat=fg3]").text());
        Integer pts = stringConversion(stat.select("[data-stat=pts]").text());
        Integer trb = stringConversion(stat.select("[data-stat=trb]").text());
        Integer ast = stringConversion(stat.select("[data-stat=ast]").text());
        Integer stl = stringConversion(stat.select("[data-stat=stl]").text());
        Integer blk = stringConversion(stat.select("[data-stat=blk]").text());
        Integer tov = stringConversion(stat.select("[data-stat=tov]").text());

        Player player = null;
        PlayerStats p = null;
        try {
            player = playerService.findPlayer(name);
            p = statsBuilder(player, game_date, fieldGoal, fgA, three, pts, trb, ast, stl, blk, tov);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            statsRepository.save(p);
            player.recordPlayerStat(p);
        }
    }

    private PlayerStats statsBuilder(Player player, LocalDate date, Integer fg, Integer fga, Integer threeP,
                                     Integer pts, Integer trb, Integer ast, Integer stl, Integer blk, Integer tov){
        PlayerStats s = PlayerStats.builder()
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

        return s;
    }

    @Override
    public Integer largeStringToInt(String s)  {
        return StringUtils.isEmpty(s) ? 0 : Integer.parseInt(s.replaceAll(",", ""));
    }

    @Override
    public Integer stringConversion(String s)  {
        try {
            return s.isEmpty() ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String[] getBoxScores() {
        return boxScores;
    }

    public List<Element> getPlayers(){
        return players;
    }
}
