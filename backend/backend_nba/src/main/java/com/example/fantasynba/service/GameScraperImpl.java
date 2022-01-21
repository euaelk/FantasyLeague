package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.GameRepository;
import com.example.fantasynba.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GameScraperImpl implements GameScraper {

    private final GameRepository gameRepository;
    private final DateService dateService;
    private final TeamRepository teamRepository;

    @Override
    @Async
    public void fetchGameData(String month) {
        openGamesForThisMonth(month);
    }

    @Override
    public List<String> setScheduleOfGamesByMonth(String websiteUrl) throws IOException {
        List<String> schedule = new ArrayList<>();
        String refSite = "https://www.basketball-reference.com/leagues/NBA_2022_games-%s.html";
        Document doc = Jsoup.connect(websiteUrl).get();
        Element filter = doc.getElementsByClass("filter").first();
        Elements links = filter.getElementsByTag("a");
        links.stream()
                .map(link -> link.text().toLowerCase())
                .forEach(month -> schedule.add(String.format(refSite, month)));
        return schedule;
    }

    @Override
    public void openGamesForThisMonth(String url)  {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            processSchedule(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processSchedule(Document doc)  {
        Element table = doc.getElementById("schedule");
        Element body = table.select("tbody").first();
        Elements games = body.select("tr");
        games.stream().filter(p -> !p.hasAttr(".thead"))
                .forEach(e -> gameDataFromHtml(e));
    }

    @Override
    public void gameDataFromHtml(Element e)  {
        LocalDate date = dateService.getDateFromString(e.select("[data-stat=date_game]").text());
        String time = e.select("[data-stat=game_start_time]").text();
        Integer hPts = stringToInt(e.select("[data-stat=home_pts]").text());
        String overtime = e.select("[data-stat=overtimes]").text();
        Integer attendance = stringToInt(e.select("[data-stat=attendance]").text());
        Integer vPts = stringToInt(e.select("[data-stat=visitor_pts]").text());
        Team visitor = teamRepository.findByName(e.select("[data-stat=visitor_team_name]").text());
        Team home = teamRepository.findByName(e.select("[data-stat=home_team_name]").text());

        Game g = createGame(date, time, hPts, overtime, attendance, vPts, visitor, home);
        Game game_duplicate = findGame(date, visitor, home);

        if (game_duplicate != null && game_duplicate.equals(g)) { return; }
        else { gameRepository.save(g); }

    }

    @Override
    public Game createGame(LocalDate date, String time, Integer hPts, String overtime, Integer attendance,
                           Integer vPts, Team visitor, Team home) {

        return Game.builder()
                .vPts(vPts)
                .hPts(hPts)
                .attendance(attendance)
                .visitor(visitor)
                .visitor_name(visitor.getName())
                .home(home)
                .home_name(home.getName())
                .time(time)
                .date(date)
                .overtime(overtime)
                .build();
    }

    @Override
    public List<Game> getAllGames() {return gameRepository.findAll();}

    @Override
    public Game findGame(LocalDate date, Team visitor, Team home){
        return gameRepository.findByDateAndTeams(date, visitor.getName(), home.getName());
    }

    @Override
    public Integer stringToInt(String s)  {
        return StringUtils.isEmpty(s) ? 0 : Integer.parseInt(s.replaceAll(",", ""));
    }

}
