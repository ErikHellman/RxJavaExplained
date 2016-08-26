package se.hellsoft.rxjavaexplained.polling;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.hellsoft.rxjavaexplained.R;

public class NetworkPollingActivity extends AppCompatActivity {

  private static final String KITTENS_CACHE_FILE = "kittenCache.json";
  Queue<Subscription> subscriptions = new LinkedList<>();
  private NetworkPolling networkPolling;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_network_polling);
    networkPolling = new NetworkPolling(new File(getFilesDir(), KITTENS_CACHE_FILE));
  }

  public void onSubscribeClicked(View view) {
    Subscription subscription = networkPolling
        .getKittens()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnUnsubscribe(() -> Log.d("NetworkPolling", "Unsubscribed!"))
        .doOnSubscribe(() -> Log.d("NetworkPolling", "Subscribed!"))
        .subscribe(kittens -> {
          Log.d("NetworkPolling", "Received " + kittens.size() + " kittens!");
        });
    subscriptions.offer(subscription);
  }

  public void onUnsubscribedClicked(View view) {
    Subscription subscription = subscriptions.poll();
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }
}
