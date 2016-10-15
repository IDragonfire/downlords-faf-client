package com.faforever.client.relay.event;

import com.faforever.client.game.Game;

public class GameFullEvent {
  private final Game game;

  public GameFullEvent(Game game) {
    this.game = game;
  }

  public Game getGame() {
    return game;
  }
}
