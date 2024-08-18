package ru.panic.dotastats.botListener;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.springframework.stereotype.Component;
import ru.panic.dotastats.component.Dota2HeroStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiscordCommandAutoCompleteListener extends ListenerAdapter {

    private final Dota2HeroStorage dota2HeroStorage;

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        //counter command auto-complete
        if (event.getName().equals("counter")) {
            if (event.getFocusedOption().getName().equals("hero_name")) {
                List<Command.Choice> options = dota2HeroStorage.getHeroes().stream()
                        .filter(name -> name.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                        .map(name -> new Command.Choice(name, name))
                        .limit(25)
                        .collect(Collectors.toList());

                event.replyChoices(options).queue();
            }
        }
    }
}
