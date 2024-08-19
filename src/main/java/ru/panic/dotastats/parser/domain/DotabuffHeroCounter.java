package ru.panic.dotastats.parser.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DotabuffHeroCounter {
    private String name;
    private double disadvantage;
    private double overOtherWinRate;
}
