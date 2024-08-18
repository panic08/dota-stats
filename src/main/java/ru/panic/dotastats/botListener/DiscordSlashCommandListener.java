package ru.panic.dotastats.botListener;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.stereotype.Component;
import ru.panic.dotastats.component.Dota2HeroStorage;
import ru.panic.dotastats.parser.DotabuffParser;
import ru.panic.dotastats.parser.domain.Dota2HeroCounter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscordSlashCommandListener extends ListenerAdapter {

    private final Dota2HeroStorage dota2HeroStorage;
    private final DotabuffParser dotabuffParser;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "counter" -> {
                onSlashCounterCommand(event);
            }
        }
    }

    private void onSlashCounterCommand(SlashCommandInteractionEvent event) {
        String heroName = event.getOption("hero_name", OptionMapping::getAsString);

        //check hero on exist
        if (!dota2HeroStorage.getHeroes().contains(heroName)) {
            event.reply("**Героя** **`" + heroName + "` не существует**\n\nВыберите другого героя из списка")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String cleanedHeroName = heroName.replaceAll("[^a-zA-Z ]", "");

        String refactoredHeroName = cleanedHeroName.replaceAll(" ", "-").toLowerCase();

        List<Dota2HeroCounter> dota2HeroCounters = dotabuffParser.getHeroCounters(refactoredHeroName, 10);

        StringBuilder messageText = new StringBuilder("**Контрпики героя `" + heroName + "`**\n\n")
                .append("Вот список героев, которые являются сильными контрпиками для **`").append(heroName).append("`**:\n\n");

        for (int i = 0; i < dota2HeroCounters.size(); i++) {
            Dota2HeroCounter counter = dota2HeroCounters.get(i);

            messageText.append((i + 1)).append(". **`").append(counter.getName()).append("`** - Преимущество: `").append(counter.getDisadvantage()).append("%`, Винрейт `").append(heroName).append("` против `").append(counter.getName()).append("`: `").append(counter.getOverOtherWinRate()).append("%`\n");
        }

        messageText.append("\n").append("Эти герои могут представлять значительную угрозу для **`").append(heroName).append("`**");

        event.reply(messageText.toString()).queue();
    }
}
