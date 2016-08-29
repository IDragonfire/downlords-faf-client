package com.faforever.client.coop;

import com.faforever.client.fx.StringListCell;
import com.faforever.client.game.GameService;
import com.faforever.client.game.NewGameInfo;
import com.faforever.client.i18n.I18n;
import com.faforever.client.map.MapService;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.user.event.LoginSuccessEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static com.faforever.client.game.GameType.COOP;
import static java.util.Collections.emptySet;
import static javafx.collections.FXCollections.observableList;

public class CoopController {

  public Node coopRoot;
  public Label titleLabel;
  public Label descriptionLabel;
  public AnchorPane gamesContainer;
  public ComboBox<CoopMissionBean> missionComboBox;
  public ImageView mapImageView;

  @Resource
  GameService gameService;
  @Resource
  CoopService coopService;
  @Resource
  EventBus eventBus;
  @Resource
  NotificationService notificationService;
  @Resource
  I18n i18n;
  @Resource
  ReportingService reportingService;
  @Resource
  MapService mapService;

  private CoopMissionBean selectedMission;

  @FXML
  void initialize() {
    missionComboBox.setCellFactory(param -> new StringListCell<>(CoopMissionBean::getName));
    missionComboBox.setButtonCell(new StringListCell<>(CoopMissionBean::getName));
    missionComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setSelectedMission(newValue));
  }

  private void setSelectedMission(CoopMissionBean mission) {
    Platform.runLater(() -> {
      this.selectedMission = mission;
      titleLabel.setText(mission.getName());
      descriptionLabel.setText(mission.getDescription());
      mapImageView.setImage(mapService.loadSmallPreview(mission.getMapFolderName()));
    });
  }

  @PostConstruct
  void postConstruct() {
    eventBus.register(this);
  }

  @Subscribe
  public void onLoginSuccess(LoginSuccessEvent event) {
    coopService.getCoopMaps().thenAccept(coopMaps -> {
      missionComboBox.setItems(observableList(coopMaps));

      SingleSelectionModel<CoopMissionBean> selectionModel = missionComboBox.getSelectionModel();
      if (selectionModel.isEmpty()) {
        selectionModel.selectFirst();
      }
    }).exceptionally(throwable -> {
      notificationService.addPersistentErrorNotification(throwable, "coop.couldNotLoad", throwable.getLocalizedMessage());
      return null;
    });
  }

  public void onPlayButtonClicked() {
    // FIXME let user specify the password
    // FIXME let user specify the game title
    gameService.hostGame(new NewGameInfo(titleLabel.getText(), null, COOP.getString(), selectedMission.getMapFolderName(), emptySet()));
  }

  public Node getRoot() {
    return coopRoot;
  }
}
