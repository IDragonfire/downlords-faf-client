package com.faforever.client.coop;

import com.faforever.client.game.GameService;
import com.faforever.client.game.NewGameInfo;
import com.faforever.client.test.AbstractPlainJavaFxTest;
import com.faforever.client.user.event.LoginSuccessEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.util.ReflectionUtils;

import static com.natpryce.hamcrest.reflection.HasAnnotationMatcher.hasAnnotation;
import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoopControllerTest extends AbstractPlainJavaFxTest {
  private CoopController instance;
  @Mock
  private CoopService coopService;
  @Mock
  private GameService gameService;
  @Mock
  private EventBus eventBus;

  @Before
  public void setUp() throws Exception {
    instance = loadController("coop.fxml");
    instance.coopService = coopService;
    instance.gameService = gameService;
    instance.eventBus = eventBus;

    instance.postConstruct();
  }

  @Test
  public void onLoginSuccess() throws Exception {
    when(coopService.getCoopMaps()).thenReturn(completedFuture(singletonList(new CoopMissionBean())));

    instance.onLoginSuccess(new LoginSuccessEvent("junit"));

    verify(coopService).getCoopMaps();
    assertThat(instance.missionComboBox.getItems(), hasSize(1));
  }

  @Test
  public void testSubscribeLoginSuccessEvent() {
    assertThat(ReflectionUtils.findMethod(instance.getClass(), "onLoginSuccess", LoginSuccessEvent.class),
        hasAnnotation(Subscribe.class));
  }

  @Test
  public void testEventBusRegistered() throws Exception {
    verify(eventBus).register(instance);
  }

  @Test
  public void onPlayButtonClicked() throws Exception {
    when(coopService.getCoopMaps()).thenReturn(completedFuture(singletonList(new CoopMissionBean())));
    instance.onLoginSuccess(new LoginSuccessEvent("junit"));
    instance.onPlayButtonClicked();

    ArgumentCaptor<NewGameInfo> captor = ArgumentCaptor.forClass(NewGameInfo.class);
    verify(gameService).hostGame(captor.capture());

    NewGameInfo newGameInfo = captor.getValue();
    assertThat(newGameInfo.getGameType(), is("coop"));
  }

  @Test
  public void testGetRoot() throws Exception {
    assertThat(instance.getRoot(), is(instance.coopRoot));
    assertThat(instance.getRoot().getParent(), is(nullValue()));
  }
}