package ru.panic.dotastats.configuration;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import ru.panic.dotastats.botListener.DiscordCommandAutoCompleteListener;
import ru.panic.dotastats.botListener.DiscordSlashCommandListener;

@Configuration
@RequiredArgsConstructor
public class DiscordBotInitializer {
    @Value("${discord.bot.token}")
    private String token;

    private final DiscordSlashCommandListener discordSlashCommandListener;
    private final DiscordCommandAutoCompleteListener discordCommandAutoCompleteListener;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        JDA jda = JDABuilder.createDefault(token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(discordCommandAutoCompleteListener)
                .addEventListeners(discordSlashCommandListener)
                .build();

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("counter", "Заставляет бота вернуть контрпики героя по заданному hero_name")
                        .addOption(OptionType.STRING, "hero_name", "Имя героя контрпики которого требуется вернуть", true, true),
                Commands.slash("meta", "Заставляет бота вернуть текущую мету по заданной роли")
                        .addOption(OptionType.STRING, "role", "Роль мету которой требуется вернуть", true, true)
        ).queue();
    }
}
