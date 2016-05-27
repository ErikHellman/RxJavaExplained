package se.hellsoft.rxjavaexplained.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import se.hellsoft.rxjavaexplained.R;
import se.hellsoft.rxjavaexplained.sqlbrite.Kitten;
import se.hellsoft.rxjavaexplained.sqlbrite.KittenAdapter;

/**
 * Silly demo of using a Subject in RxJava as an EventBus.
 */
public class EventBusDemo extends AppCompatActivity {

  private Subscription eventSubscription;
  private KittenAdapter kittenAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_of_kittens_layout);
    RecyclerView listOfKittens = (RecyclerView) findViewById(R.id.list_of_kittens);
    FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

    kittenAdapter = new KittenAdapter(this);
    listOfKittens.setLayoutManager(new LinearLayoutManager(this));
    listOfKittens.setAdapter(kittenAdapter);

    floatingActionButton.setOnClickListener(view ->
        startService(new Intent(EventBuilderService.ACTION_BUILD_KITTEN, null,
            EventBusDemo.this, EventBuilderService.class)));
  }

  @Override
  protected void onResume() {
    super.onResume();
    eventSubscription = ((MyApp) getApplication()).getSubject()
        // Filter out anything but kittens
        .filter(event -> {
          Log.d("EventBusDemo", "Filtering " + event);
          return event.type.equals(Event.Type.Kitten);
        })
        // Convert Event to Kitten
        .map(event -> {
          Log.d("EventBusDemo", "Mapping Event to Kitten");
          Kitten kitten = new Kitten();
          kitten.name = event.name;
          kitten.description = event.description;
          return kitten;
        })
        // Switch back to main thread
        .observeOn(AndroidSchedulers.mainThread())
        // Add the new Kitten to our list
        .subscribe(kittenAdapter::addKitten);
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (eventSubscription != null && !eventSubscription.isUnsubscribed()) {
      eventSubscription.unsubscribe();
    }
  }
}

