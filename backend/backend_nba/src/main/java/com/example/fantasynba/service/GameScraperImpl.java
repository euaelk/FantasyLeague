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
import java.util.concurrent.CompletableFuture;



@Service
@Transactional
@RequiredArgsConstructor
public class GameScraperImpl implements GameScraper {

    private final GameRepository gameRepository;
    private final DateService dateService;
    private final TeamRepository teamRepository;
    static Map<String, String> schedule;

    @Override
    @Async
    public void fetchGameData() {
        schedule = new HashMap<>();
        try {
            filterMonthlyGames("https://www.basketball-reference.com/leagues/NBA_2022_games.html");
            for (String s : schedule.values()){
                CompletableFuture<Document> cf = returnUrl(s);
                CompletableFuture<Void> processFuture = cf
                        .thenAccept(f -> processSchedule(f));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    @Async
    public void filterMonthlyGames(final String websiteUrl) throws IOException {
        Document doc = Jsoup.connect(websiteUrl).get();
        Element filter = doc.getElementsByClass("filter").first();
        Elements links = filter.getElementsByTag("a");
        for (Element link : links){
            String linkHref = link.attr("href");
            String month = link.text();
            schedule.put(month, websiteUrl.concat(linkHref));
        }
    }

    @Async
    public CompletableFuture<Document> returnUrl(String url)  {
        try {
            Document doc = Jsoup.connect(url).get();
            return CompletableFuture.completedFuture(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processSchedule(Document doc){
        Element table = doc.getElementById("schedule");
        Iterator<Element> row = table != null ? table.select("tr").iterator() : null;
        row.next(); // skip <th>
        while (row.hasNext()){
            Iterator<Element> game = row.next().getAllElements().iterator();
            gameDataFromHtml(game.next());
        }
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

        if (game_duplicate == null || !game_duplicate.equals(g)) { gameRepository.save(g); }

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
