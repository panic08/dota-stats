package ru.panic.dotastats.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.panic.dotastats.parser.domain.StratzProfile;

@Service
@RequiredArgsConstructor
public class StratzParser {

    private final RestTemplate restTemplate;
    private final String FAKE_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36";
    private final String STRATZ_URL = "https://stratz.com/";

    public StratzProfile getProfile(long steam32Id, boolean excludeTurbo) {
        //todo
        return null;
    }
}