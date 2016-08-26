package se.hellsoft.rxjavaexplained.polling;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import se.hellsoft.rxjavaexplained.sqlbrite.Kitten;

// Fake Retrofit service :)
public class KittensApi {
  public static final Kitten[] KITTENS = new Kitten[100];
  public static final Random RANDOM = new Random();

  static {
    for (int i = 0; i < KITTENS.length; i++) {
      KITTENS[i] = new Kitten(i, "Kitten " + i, "Description for Kitten " + i);
    }
  }

  public static Observable<List<Kitten>> getAllKittens() {
    // Return random set of kittens...
    return Observable.from(KITTENS).filter(kitten -> RANDOM.nextBoolean()).toList();
  }

}
