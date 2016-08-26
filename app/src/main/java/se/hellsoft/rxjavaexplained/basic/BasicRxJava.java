package se.hellsoft.rxjavaexplained.basic;

import android.os.SystemClock;
import android.util.Log;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BasicRxJava {
  public static final String IPSUM_LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

  public void splitWords() {
    List<String> words = Observable
        .just(IPSUM_LOREM)
        .flatMap(word -> Observable.from(word.split(" ")))
        .toList()
        .toBlocking()
        .first();
  }

  public void wordLengths() {
    List<Integer> wordsLengths = Observable
        .just(IPSUM_LOREM)
        .flatMap(word -> Observable.from(word.split(" ")))
        .map(String::length)
        .toList()
        .toBlocking()
        .first();
  }

  public void minFourLetterWords() {
    List<String> fourLetterWords = Observable
        .just(IPSUM_LOREM)
        .flatMap(word -> Observable.from(word.split(" ")))
        .filter(word -> word.length() >= 4)
        .toList()
        .toBlocking()
        .first();
  }

  public void subscribing() {
    Subscription subscription = Observable
        .just(IPSUM_LOREM)
        .flatMap(s -> Observable.from(s.split(" ")))
        .subscribe(word -> {
          Log.d("BasicRxJava", "Got word: " + word);
        });

//    Remember to unsubscribe when done!
//    subscription.unsubscribe();
  }

  public void backgroundProcessing() {
    Subscription subscription = Observable
        .just(IPSUM_LOREM)
        .flatMap(s -> Observable.from(s.split(" ")))
        .map(new Func1<String, String>() {
          @Override
          public String call(String s) {
            SystemClock.sleep(1000); // Fake long running call
            return s.toUpperCase();
          }
        })
        // Do all the work on a background thread
        .subscribeOn(Schedulers.computation())
        // Observe (deliver) events on the main thread
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(word -> {
          Log.d("BasicRxJava", "Got word: " + word);
        });
  }

  public void zipping() {
    Observable<String> wordObservable = Observable
        .just(IPSUM_LOREM)
        .flatMap(s -> Observable.from(s.split(" ")));

    Observable<Integer> wordLengthObservable = wordObservable.map(String::length);
    Observable.zip(wordObservable, wordLengthObservable,
        (word, length) -> String.format("%s is %d long", word, length))
        .subscribe(wordWithLength -> Log.d("BasicRxJava", wordWithLength));
  }
}
