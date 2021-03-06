package com.github.avalon.player.command;

import com.github.avalon.chat.command.CommandExecutor;
import com.github.avalon.chat.command.CommandListener;
import com.github.avalon.chat.command.annotation.CommandPerformer;
import com.github.avalon.chat.message.TranslatedMessage;
import com.github.avalon.player.IPlayer;
import com.github.avalon.player.attributes.GameMode;
import org.apache.commons.lang3.StringUtils;

public class GamemodeCommand extends CommandListener {

  public GamemodeCommand() {
    register("gamemode", this::gamemode);
  }

  @CommandPerformer(command = "gamemode")
  public void gamemode(CommandExecutor executor) {
    if (executor.getSender() instanceof IPlayer) {
      IPlayer player = (IPlayer) executor.getSender();

      if (executor.getArguments().length < 1) {
        player.sendSystemMessage(new TranslatedMessage("command.not_enough_arguments"));
      } else if (executor.getArguments().length == 1) {
        String gamemodeArgument = executor.getArguments()[0];

        boolean number = StringUtils.isNumeric(gamemodeArgument);
        GameMode gameMode =
            number
                ? GameMode.getByIndex(Integer.parseInt(gamemodeArgument))
                : GameMode.getByName(gamemodeArgument);

        player.setGameMode(gameMode);
        player.sendSystemMessage(new TranslatedMessage("gamemode.change", StringUtils.capitalize(gameMode.name().toLowerCase())));
      } else {

      }
    }
  }
}
