package com.faforever.client.coop;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CoopService {
  CompletableFuture<List<CoopMissionBean>> getCoopMaps();
}
