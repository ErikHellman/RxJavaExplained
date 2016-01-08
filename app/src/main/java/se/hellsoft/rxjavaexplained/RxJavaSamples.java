package se.hellsoft.rxjavaexplained;

import rx.Observable;
import rx.Subscription;

public class RxJavaSamples {
  private static String[] DATA = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

  public Observable<String> getData() {
    return Observable.from(DATA);
  }

  public void printData() {
    Subscription dataSubscription = getData()
        .map(String::toLowerCase)
        .subscribe(System.out::println);
  }
}
