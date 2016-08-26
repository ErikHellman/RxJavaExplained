package se.hellsoft.rxjavaexplained.polling;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import rx.Observable;
import se.hellsoft.rxjavaexplained.sqlbrite.Kitten;

public class NetworkPolling {
  private final JsonAdapter<List<Kitten>> jsonAdapter;
  private List<Kitten> cachedKittens = Collections.emptyList();
  private Observable<List<Kitten>> kittensObservable;
  private File cacheFile;

  public NetworkPolling(@NonNull File cacheFile) {
    this.cacheFile = cacheFile;
    Moshi moshi = new Moshi.Builder().build();
    jsonAdapter = moshi.adapter(Types.newParameterizedType(List.class, Kitten.class));
    kittensObservable = Observable
        .interval(0, 5, TimeUnit.SECONDS)
        // Read from cached file at first subscription
        .doOnSubscribe(this::readKittensFromCache)
        // For each interval, call our service
        .flatMap(interval -> KittensApi.getAllKittens())
        // Return the cached data instead of empty response
        .map(kittens -> kittens == null || kittens.size() == 0 ?
            cachedKittens : kittens)
        // Update our cache
        .doOnNext(this::writeKittensToCache)
        // Return the cached version on error
        .onErrorReturn(throwable -> cachedKittens)
        // Multicasts the data from the original to all subscribers. Alias for publish().refCount()
        .share()
        // Start with the cached data
        .startWith(cachedKittens)
    ;
  }

  private synchronized void writeKittensToCache(List<Kitten> kittens) {
    Log.d("NetworkPolling", "writeKittensToCache");
    if (kittens == null || kittens.isEmpty()) {
      Log.d("NetworkPolling", "Don't write empty data to cache!");
      return; // Don't write empty responses
    }

    cachedKittens = kittens;

    try (BufferedSink bufferedSink = Okio.buffer(Okio.sink(cacheFile))) {
      jsonAdapter.toJson(bufferedSink, kittens);
      Log.d("NetworkPolling", cachedKittens.size() + " kittens written to cache file.");
    } catch (IOException e) {
      Log.e("NetworkPolling", "Failed to write to cache.", e);
    }
  }

  private synchronized void readKittensFromCache() {
    Log.d("NetworkPolling", "readKittensFromCache");
    if (cachedKittens != null && !cachedKittens.isEmpty()) {
      Log.d("NetworkPolling", "Kitten cache not empty - has "
          + cachedKittens.size() + " kittens!");
      return; // Don't read from file if cache is already populated
    }

    try (BufferedSource bufferedSource = Okio.buffer(Okio.source(cacheFile))) {
      cachedKittens = jsonAdapter.fromJson(bufferedSource);
      Log.d("NetworkPolling", "Read " + cachedKittens.size() + " kittens from cache file.");
    } catch (IOException e) {
      Log.e("NetworkPolling", "Failed to read from cache.", e);
    }
  }

  public Observable<List<Kitten>> getKittens() {
    return kittensObservable;
  }

}
