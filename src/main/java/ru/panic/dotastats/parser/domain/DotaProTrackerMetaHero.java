package ru.panic.dotastats.parser.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DotaProTrackerMetaHero {
    private String name;

    private double winRate;
}
