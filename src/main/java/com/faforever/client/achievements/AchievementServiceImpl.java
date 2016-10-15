package com.faforever.client.achievements;

import com.faforever.client.api.AchievementDefinition;
import com.faforever.client.api.FafApiAccessor;
import com.faforever.client.api.PlayerAchievement;
import com.faforever.client.player.Player;
import com.faforever.client.config.CacheNames;
import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.player.PlayerService;
import com.faforever.client.remote.FafService;
import com.faforever.client.remote.UpdatedAchievementsMessage;
import com.faforever.client.theme.ThemeService;
import com.faforever.client.user.UserService;
import com.google.common.base.Strings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.springframework.cache.annotation.Cacheable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadPoolExecutor;

public class AchievementServiceImpl implements AchievementService {

  private static final int ACHIEVEMENT_IMAGE_SIZE = 128;
  private final ObservableList<PlayerAchievement> readOnlyPlayerAchievements;
  private final ObservableList<PlayerAchievement> playerAchievements;

  @Resource
  UserService userService;
  @Resource
  FafApiAccessor fafApiAccessor;
  @Resource
  FafService fafService;
  @Resource
  NotificationService notificationService;
  @Resource
  I18n i18n;
  @Resource
  PlayerService playerService;
  @Resource
  ThemeService themeService;
  @Resource
  ThreadPoolExecutor threadPoolExecutor;

  public AchievementServiceImpl() {
    playerAchievements = FXCollections.observableArrayList();
    readOnlyPlayerAchievements = FXCollections.unmodifiableObservableList(playerAchievements);
  }

  @Override
  public CompletionStage<List<PlayerAchievement>> getPlayerAchievements(String username) {
    if (userService.getUsername().equalsIgnoreCase(username)) {
      if (readOnlyPlayerAchievements.isEmpty()) {
        reloadAchievements();
      }
      return CompletableFuture.completedFuture(readOnlyPlayerAchievements);
    }

    Player playerForUsername = playerService.getPlayerForUsername(username);
    if (playerForUsername == null) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }
    int playerId = playerForUsername.getId();
    return CompletableFuture.supplyAsync(() -> FXCollections.observableList(fafApiAccessor.getPlayerAchievements(playerId)), threadPoolExecutor);
  }

  @Override
  public CompletionStage<List<AchievementDefinition>> getAchievementDefinitions() {
    return CompletableFuture.supplyAsync(() -> fafApiAccessor.getAchievementDefinitions(), threadPoolExecutor);
  }

  @Override
  public CompletionStage<AchievementDefinition> getAchievementDefinition(String achievementId) {
    return CompletableFuture.supplyAsync(() -> fafApiAccessor.getAchievementDefinition(achievementId), threadPoolExecutor);
  }

  @Override
  @Cacheable(CacheNames.ACHIEVEMENT_IMAGES)
  public Image getRevealedIcon(AchievementDefinition achievementDefinition) {
    if (Strings.isNullOrEmpty(achievementDefinition.getRevealedIconUrl())) {
      return themeService.getThemeImage(ThemeService.DEFAULT_ACHIEVEMENT_IMAGE);
    }
    return new Image(achievementDefinition.getRevealedIconUrl(), ACHIEVEMENT_IMAGE_SIZE, ACHIEVEMENT_IMAGE_SIZE, true, true, true);
  }

  @Override
  @Cacheable(CacheNames.ACHIEVEMENT_IMAGES)
  public Image getUnlockedIcon(AchievementDefinition achievementDefinition) {
    if (Strings.isNullOrEmpty(achievementDefinition.getUnlockedIconUrl())) {
      return themeService.getThemeImage(ThemeService.DEFAULT_ACHIEVEMENT_IMAGE);
    }
    return new Image(achievementDefinition.getUnlockedIconUrl(), ACHIEVEMENT_IMAGE_SIZE, ACHIEVEMENT_IMAGE_SIZE, true, true, true);
  }

  private void reloadAchievements() {
    playerAchievements.setAll(fafApiAccessor.getPlayerAchievements(userService.getUid()));
  }

  @PostConstruct
  void postConstruct() {
    fafService.addOnMessageListener(UpdatedAchievementsMessage.class, updatedAchievementsMessage -> reloadAchievements());
  }
}
