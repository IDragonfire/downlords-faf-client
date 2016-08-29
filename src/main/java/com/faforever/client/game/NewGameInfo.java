package com.faforever.client.game;

import java.util.Set;

public class NewGameInfo {

  private String title;
  private String password;
  private String gameType;
  private String map;
  private Set<String> simMods;

  public NewGameInfo() {
  }

  public NewGameInfo(String title, String password, String gameType, String map, Set<String> simMods) {
    this.title = title;
    this.password = password;
    this.gameType = gameType;
    this.map = map;
    this.simMods = simMods;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getGameType() {
    return gameType;
  }

  public void setGameType(String mod) {
    this.gameType = mod;
  }

  public String getMap() {
    return map;
  }

  public void setMap(String map) {
    this.map = map;
  }

  public Set<String> getSimMods() {
    return simMods;
  }

  public void setSimMods(Set<String> simMods) {
    this.simMods = simMods;
  }
}
