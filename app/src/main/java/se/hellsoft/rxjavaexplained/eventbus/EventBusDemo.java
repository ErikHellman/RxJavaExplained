package se.hellsoft.rxjavaexplained.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import se.hellsoft.rxjavaexplained.R;
import se.hellsoft.rxjavaexplained.sqlbrite.Kitten;
import se.hellsoft.rxjavaexplained.sqlbrite.KittenAdapter;

/**
 * Silly demo of using a Subject in RxJava as an EventBus.
 */
public class EventBusDemo extends AppCompatActivity {
    @Bind(R.id.list_of_kittens)
    RecyclerView mListOfKittens;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private Subscription mEventSubscription;
    private KittenAdapter mKittenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_kittens_layout);
        ButterKnife.bind(this);

        mKittenAdapter = new KittenAdapter(this);
        mListOfKittens.setLayoutManager(new LinearLayoutManager(this));
        mListOfKittens.setAdapter(mKittenAdapter);

        mFab.setOnClickListener(view -> {
            startService(new Intent(EventBuilderService.ACTION_BUILD_KITTEN, null,
                    EventBusDemo.this, EventBuilderService.class));
        });

        mEventSubscription = ((MyApp) getApplication()).getSubject()
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
                        // Add the new Kitten to our list
                .subscribe(mKittenAdapter::addKitten);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!mEventSubscription.isUnsubscribed()) {
            mEventSubscription.unsubscribe();
        }
    }
}

