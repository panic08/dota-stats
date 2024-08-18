package ru.panic.dotastats.parser;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.panic.dotastats.parser.domain.Dota2HeroCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DotabuffParser {

    private final RestTemplate restTemplate;
    private final String DOTABUFF_URL = "https://www.dotabuff.com";
    private final String FAKE_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36";

    public List<Dota2HeroCounter> getHeroCounters(String heroName, int limit) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", FAKE_USER_AGENT);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                DOTABUFF_URL + "/heroes/" + heroName + "/counters",
                HttpMethod.GET,
                entity,
                String.class
        );

        Document dotabuffDocument = Jsoup.parse(Objects.requireNonNull(response.getBody()));
        Element matchupsTable = dotabuffDocument.getElementsByClass("sortable").first();

        List<Element> heroesMatchups = matchupsTable.getElementsByTag("tbody").first()
                .getElementsByTag("tr");

        List<Dota2HeroCounter> dota2HeroCounters = new ArrayList<>();

        heroesMatchups.stream()
                .limit(limit)
                .forEach(heroMatchup -> {
                    List<Element> dataAttributes = heroMatchup.getElementsByAttribute("data-value");

                    dota2HeroCounters.add(Dota2HeroCounter.builder()
                            .name(dataAttributes.get(0).attr("data-value"))
                            .disadvantage(Double.parseDouble(dataAttributes.get(1).attr("data-value")))
                            .overOtherWinRate(Double.parseDouble(dataAttributes.get(2).attr("data-value")))
                            .build());
                });

        return dota2HeroCounters;
    }
}
