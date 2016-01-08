package se.hellsoft.rxjavaexplained.eventbus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

import se.hellsoft.rxjavaexplained.BuildConfig;
import se.hellsoft.rxjavaexplained.sqlbrite.Kitten;

/**
 * This can't be an IntentService as the worker thread will be terminated once the service is done
 * processing incoming Intents. To fix that you have to keep a separate thread which you emit events on,
 * or you simply emit them on the main thread as shown here.
 * <p>
 * I leave the more advanced (and probably more useful) example to the reader :)
 */
public class EventBuilderService extends Service {
  public static final String ACTION_BUILD_KITTEN = BuildConfig.APPLICATION_ID + ".action.BUILD_KITTEN";
  private static final Random RANDOM = new Random();

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    ((MyApp) getApplication()).getSubject().onNext(buildEvent());
    return START_NOT_STICKY;
  }

  private Event buildEvent() {
    Event event = new Event();
    switch (RANDOM.nextInt(3)) {
      case 0:
        event.type = Event.Type.Kitten;
        event.name = Kitten.CAT_NAMES[RANDOM.nextInt(Kitten.CAT_NAMES.length)];
        break;
      case 1:
        event.type = Event.Type.Dog;
        event.name = "Pluto";
        break;
      case 2:
        event.type = Event.Type.Rabbit;
        event.name = "Dinner";
        break;
    }
    event.description = "This is my animal!";
    event.timeStamp = System.currentTimeMillis();
    Toast.makeText(this, "Created new " + event.type.name(), Toast.LENGTH_SHORT).show();
    Log.d("EventBusDemo", "New event: " + event);
    return event;
  }
}
