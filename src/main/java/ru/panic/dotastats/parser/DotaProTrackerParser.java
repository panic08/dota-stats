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
import ru.panic.dotastats.parser.domain.DotaProTrackerMetaHero;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DotaProTrackerParser {

    private final RestTemplate restTemplate;
    private final String DOTA_PRO_TRACKER_URL = "https://dota2protracker.com";
    private final String FAKE_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36";

    public List<DotaProTrackerMetaHero> getMetaHeroes(String role, int limit) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", FAKE_USER_AGENT);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                DOTA_PRO_TRACKER_URL + "/_get/meta/" + role + "/html",
                HttpMethod.GET,
                entity,
                String.class
        );

        Document dotaProTrackerDocument = Jsoup.parse(Objects.requireNonNull(response.getBody()));

        List<Element> metaHeroElements = dotaProTrackerDocument.getElementsByClass("grid grid-cols-14 gap-1 gridS grid_ hero-row").stream()
                .limit(limit)
                .toList();

        List<DotaProTrackerMetaHero> dotaProTrackerMetaHeroes = metaHeroElements.stream()
                        .map(e -> {
                            String name = e.getElementsByClass("flex gap-1 items-center rounded-md hover:bg-d2pt-gray-5 p-2 text-xs text-nowrap")
                                    .first().select("span")
                                    .text();

                            String winRate = e.getElementsByClass("green font-medium").first().text();

                            double cleanWinRate = Double.parseDouble(winRate.substring(0, winRate.length() - 1));

                            return DotaProTrackerMetaHero.builder()
                                    .name(name)
                                    .winRate(cleanWinRate)
                                    .build();
                        })
                .toList();

        return dotaProTrackerMetaHeroes;
    }
}
