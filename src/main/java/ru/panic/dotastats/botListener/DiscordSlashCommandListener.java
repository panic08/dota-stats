package ru.panic.dotastats.botListener;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.stereotype.Component;
import ru.panic.dotastats.component.Dota2HeroStorage;
import ru.panic.dotastats.parser.DotaProTrackerParser;
import ru.panic.dotastats.parser.DotabuffParser;
import ru.panic.dotastats.parser.domain.DotaProTrackerMetaHero;
import ru.panic.dotastats.parser.domain.DotabuffHeroCounter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscordSlashCommandListener extends ListenerAdapter {

    private final Dota2HeroStorage dota2HeroStorage;
    private final DotabuffParser dotabuffParser;
    private final DotaProTrackerParser dotaProTrackerParser;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "counter" -> onSlashCounterCommand(event);
            case "meta" -> onSlashMetaCommand(event);
        }
    }

    private void onSlashMetaCommand(SlashCommandInteractionEvent event) {
        String role = event.getOption("role", OptionMapping::getAsString);

        //check role on exist
        if (!role.equals("Керри") && !role.equals("Мидер") && !role.equals("Хардлейнер") && !role.equals("Поддержка") && !role.equals("Полная поддержка")) {
            event.reply("**Роли** `" + role + "` **не существует**\n\nВыберите другую роль из списка")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String refactoredRole = null;

        switch (role) {
            case "Керри" -> refactoredRole = "pos-1";
            case "Мидер" -> refactoredRole = "pos-2";
            case "Хардлейнер" -> refactoredRole = "pos-3";
            case "Поддержка" -> refactoredRole = "pos-4";
            case "Полная поддержка" -> refactoredRole = "pos-5";
        }

        List<DotaProTrackerMetaHero> dotaProTrackerMetaHeroes = (List<DotaProTrackerMetaHero>) dotaProTrackerParser.getMetaHeroes(refactoredRole, 8);

        StringBuilder messageText = new StringBuilder("**Текущая мета для роли `" + role + "`**\n\n")
                .append("Вот список героев, который являются метовыми для роли **`").append(role).append("`**:\n\n");

        for (int i = 0; i < dotaProTrackerMetaHeroes.size(); i++) {
            DotaProTrackerMetaHero metaHero = dotaProTrackerMetaHeroes.get(i);

            messageText.append(i).append(". **`").append(metaHero.getName()).append("`** - Винрейт: `").append(metaHero.getWinRate()).append("%`\n");
        }

        event.reply(messageText.toString()).setEphemeral(true).queue();
    }

    private void onSlashCounterCommand(SlashCommandInteractionEvent event) {
        String heroName = event.getOption("hero_name", OptionMapping::getAsString);

        //check hero on exist
        if (!dota2HeroStorage.getHeroes().contains(heroName)) {
            event.reply("**Героя** `" + heroName + "` **не существует**\n\nВыберите другого героя из списка")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String cleanedHeroName = heroName.replaceAll("[^a-zA-Z ]", "");

        String refactoredHeroName = cleanedHeroName.replaceAll(" ", "-").toLowerCase();

        List<DotabuffHeroCounter> dotabuffHeroCounters = dotabuffParser.getHeroCounters(refactoredHeroName, 10);

        //rounding winRates and other stats
        for (DotabuffHeroCounter heroCounter : dotabuffHeroCounters) {
            heroCounter.setDisadvantage(Math.round(heroCounter.getDisadvantage() * 10.0) / 10.0);
            heroCounter.setOverOtherWinRate(Math.round(heroCounter.getOverOtherWinRate() * 10.0) / 10.0);
        }

        StringBuilder messageText = new StringBuilder("**Контрпики героя `" + heroName + "`**\n\n")
                .append("Вот список героев, которые являются сильными контрпиками для **`").append(heroName).append("`**:\n\n");

        for (int i = 0; i < dotabuffHeroCounters.size(); i++) {
            DotabuffHeroCounter counter = dotabuffHeroCounters.get(i);

            messageText.append((i + 1)).append(". **`").append(counter.getName()).append("`** - Преимущество: `").append(counter.getDisadvantage()).append("%`, Винрейт `").append(heroName).append("` против `").append(counter.getName()).append("`: `").append(counter.getOverOtherWinRate()).append("%`\n");
        }

        messageText.append("\n").append("Эти герои могут представлять значительную угрозу для **`").append(heroName).append("`**");

        event.reply(messageText.toString()).setEphemeral(true).queue();
    }
}
