package ru.panic.dotastats.parser.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class StratzProfile{
    private String nickname;

    private int matchCount;

    private double winRate;

    private int winCount;

    private int loseCount;

    private Collection<StratzProfileMatch> matches;

    private Collection<StratzMostPlayedHero> mostPlayedHeroes;

    private boolean isSmurf;

    @Data
    @Builder
    public static class StratzProfileMatch {
        private String heroName;

        private DotaRole role;

        private boolean isWin;

        private boolean isSelfLineWin;

        private int killCount;

        private int deathCount;

        private int assistCount;

        private String time;
    }

    @Data
    @Builder
    public static class StratzMostPlayedHero {
        private String heroName;

        private double winRate;

        private int matchCount;
    }
}
