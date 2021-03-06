package com.faforever.client.remote.gson;

import com.faforever.client.game.Faction;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class FactionTypeAdapter extends TypeAdapter<Faction> {

  public static final FactionTypeAdapter INSTANCE = new FactionTypeAdapter();

  private FactionTypeAdapter() {

  }

  @Override
  public void write(JsonWriter out, Faction value) throws IOException {
    out.value(value.getString());
  }

  @Override
  public Faction read(JsonReader in) throws IOException {
    return Faction.fromString(in.nextString());
  }
}
