package com.example.fantasynba.service;

import com.example.fantasynba.domain.GameRequest;
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
@RequiredArgsConstructor
public class GameScraperImpl implements GameScraper {


    private final GameService gameService;
    private final GameRepository gameRepo;
    private final DateService dateService;
    private final TeamRepository teamRepository;

    @Async
    public void fetchGameData(String reference_url)  {
        Map<String, String> urls = new HashMap<>();

        try {
            String baseUri = "https://www.basketball-reference.com";
            Document doc = Jsoup.connect(reference_url).get();

            Element months = doc.getElementsByClass("filter").first();
            Elements links = months.getElementsByTag("a");

            for (Element link : links){
                String linkHref = link.attr("href");
                String month = link.text();
                urls.put(month, baseUri.concat(linkHref));
            }
            List<String> monthLinks = new ArrayList<>(urls.values());

            gameDataExtraction(monthLinks);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void gameDataExtraction(List<String> links) throws IOException {
        for (String s : links){
            Document doc = Jsoup.connect(s).get();
            Element table = doc.getElementById("schedule");
            Iterator<Element> row = table.select("tr").iterator();
            row.next(); // skip <th>
            while (row.hasNext()){
                Iterator<Element> game = row.next().getAllElements().iterator();
                createGame(game.next());
            }
        }
    }


    @Override
    public Integer stringToInt(String s)  {
        return StringUtils.isEmpty(s) ? 0 : Integer.parseInt(s.replaceAll(",", ""));
    }

    @Override
    @Transactional
    public void createGame(Element e)  {

        LocalDate date = dateService.getDateFromString(e.select("[data-stat=date_game]").text());
        String time = e.select("[data-stat=game_start_time]").text();
        Integer hPts = stringToInt(e.select("[data-stat=home_pts]").text());
        String overtime = e.select("[data-stat=overtimes]").text();
        Integer attendance = stringToInt(e.select("[data-stat=attendance]").text());
        Integer vPts = stringToInt(e.select("[data-stat=visitor_pts]").text());

        Team visitor = teamRepository.findByName(e.select("[data-stat=visitor_team_name]").text());
        Team home = teamRepository.findByName(e.select("[data-stat=home_team_name]").text());

        GameRequest gr = GameRequest.builder()
                .vPts(vPts)
                .hPts(hPts)
                .attendance(attendance)
                .visitor(visitor)
                .home(home)
                .time(time)
                .date(date)
                .overtime(overtime)
                .build();
        gameRepo.save(gameService.saveNewGame(gr));

    }



}
