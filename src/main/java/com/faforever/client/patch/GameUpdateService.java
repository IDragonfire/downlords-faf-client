package com.faforever.client.patch;

import com.faforever.client.game.FeaturedModBean;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

public interface GameUpdateService {

  /**
   * @param modVersions a map of indices ("1","2","3","4"...) to version numbers. Don't ask me what these indices map
   * to.
   * @param simModUids a list of sim mod UIDs to update
   */
  CompletionStage<Void> updateInBackground(FeaturedModBean featuredMod, Integer version, Map<String, Integer> modVersions, Set<String> simModUids);
}
