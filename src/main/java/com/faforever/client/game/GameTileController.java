package com.faforever.client.game;

import com.faforever.client.i18n.I18n;
import com.faforever.client.map.MapService;
import com.google.common.base.Joiner;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.function.Consumer;

import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.beans.binding.Bindings.createStringBinding;

public class GameTileController {

  @FXML
  Label lockIconLabel;
  @FXML
  Label gameTypeLabel;
  @FXML
  Node gameTileRoot;
  @FXML
  Label gameMapLabel;
  @FXML
  Label gameTitleLabel;
  @FXML
  Label numberOfPlayersLabel;
  @FXML
  Label hostLabel;
  @FXML
  Label modsLabel;
  @FXML
  ImageView mapImageView;

  @Resource
  MapService mapService;
  @Resource
  I18n i18n;
  @Resource
  ApplicationContext applicationContext;
  @Resource
  GameService gameService;
  @Resource
  JoinGameHelper joinGameHelper;
  private Consumer<Game> onSelectedListener;
  private Game game;

  public void setOnSelectedListener(Consumer<Game> onSelectedListener) {
    this.onSelectedListener = onSelectedListener;
  }

  @FXML
  void initialize() {
    modsLabel.managedProperty().bind(modsLabel.visibleProperty());
    modsLabel.visibleProperty().bind(modsLabel.textProperty().isNotEmpty());
    gameTypeLabel.managedProperty().bind(gameTypeLabel.visibleProperty());
    lockIconLabel.managedProperty().bind(lockIconLabel.visibleProperty());
  }

  @PostConstruct
  void postConstruct() {
    joinGameHelper.setParentNode(getRoot());
  }

  public Node getRoot() {
    return gameTileRoot;
  }

  public void setGame(Game game) {
    this.game = game;

    GameTypeBean gameType = gameService.getGameTypeByString(game.getFeaturedMod());
    String fullName = gameType != null ? gameType.getFullName() : null;
    gameTypeLabel.setText(StringUtils.defaultString(fullName));

    gameTitleLabel.setText(game.getTitle());
    hostLabel.setText(game.getHost());

    gameMapLabel.textProperty().bind(game.mapFolderNameProperty());
    numberOfPlayersLabel.textProperty().bind(createStringBinding(
        () -> i18n.get("game.players.format", game.getNumPlayers(), game.getMaxPlayers()),
        game.numPlayersProperty(),
        game.maxPlayersProperty()
    ));
    mapImageView.imageProperty().bind(createObjectBinding(() -> mapService.loadSmallPreview(game.getMapFolderName()), game.mapFolderNameProperty()));

    modsLabel.textProperty().bind(createStringBinding(
        () -> Joiner.on(i18n.get("textSeparator")).join(game.getSimMods().values()),
        game.getSimMods()
    ));

    // TODO display "unknown map" image first since loading may take a while
    mapImageView.imageProperty().bind(createObjectBinding(
        () -> mapService.loadSmallPreview(game.getMapFolderName()),
        game.mapFolderNameProperty()
    ));

    lockIconLabel.visibleProperty().bind(game.passwordProtectedProperty());

    Tooltip tooltip = new Tooltip();
    Tooltip.install(gameTileRoot, tooltip);
    tooltip.activatedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        GameTooltipController gameTooltipController = applicationContext.getBean(GameTooltipController.class);
        gameTooltipController.setGameInfoBean(game);
        tooltip.setGraphic(gameTooltipController.getRoot());
      }
    });
    tooltip.showingProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        tooltip.setGraphic(null);
      }
    });
  }

  @FXML
  void onClick(MouseEvent mouseEvent) {
    Objects.requireNonNull(onSelectedListener, "onSelectedListener has not been set");
    Objects.requireNonNull(game, "gameInfoBean has not been set");

    gameTileRoot.requestFocus();
    onSelectedListener.accept(game);

    if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
      mouseEvent.consume();
      joinGameHelper.join(game);
    }
  }
}
