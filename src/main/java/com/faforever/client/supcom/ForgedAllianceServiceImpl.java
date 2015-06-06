package com.faforever.client.supcom;

import com.faforever.client.chat.PlayerInfoBean;
import com.faforever.client.legacy.relay.LocalRelayServer;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.List;

public class ForgedAllianceServiceImpl implements ForgedAllianceService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  PreferencesService preferencesService;

  @Autowired
  UserService userService;

  @Autowired
  LocalRelayServer localRelayServer;

  @Override
  public Process startGame(int uid, String mod, List<String> additionalArgs) throws IOException {
    Path executable = preferencesService.getFafBinDirectory().resolve("ForgedAlliance.exe");

    PlayerInfoBean currentPlayer = userService.getCurrentPlayer();

    List<String> launchCommand = LaunchCommandBuilder.create()
        .executable(executable)
        .uid(uid)
        .clan(currentPlayer.getClan())
        .country(currentPlayer.getCountry())
        .deviation(currentPlayer.getDeviation())
        .mean(currentPlayer.getMean())
        .username(currentPlayer.getUsername())
        .additionalArgs(additionalArgs)
            // FIXME fix the path
        .logFile(preferencesService.getFafDataDirectory().resolve("faf.log"))
        .localGpgPort(localRelayServer.getPort())
        .build();

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.inheritIO();
    processBuilder.directory(executable.getParent().toAbsolutePath().toFile());
    processBuilder.command(launchCommand);

    logger.info("Starting Forged Alliance with command: " + processBuilder.command());

    return processBuilder.start();
  }

}