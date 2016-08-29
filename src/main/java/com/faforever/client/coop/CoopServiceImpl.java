package com.faforever.client.coop;

import com.faforever.client.remote.FafService;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CoopServiceImpl implements CoopService {

  @Resource
  FafService fafService;

  @Override
  public CompletableFuture<List<CoopMissionBean>> getCoopMaps() {
    return fafService.getCoopMaps();
  }
}
