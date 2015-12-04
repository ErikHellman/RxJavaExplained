package se.hellsoft.rxjavaexplained.eventbus;

import android.app.Application;

import rx.subjects.BehaviorSubject;

public class MyApp extends Application {

    private BehaviorSubject<Event> mSubject;

    @Override
    public void onCreate() {
        super.onCreate();
        mSubject = BehaviorSubject.create();
    }

    // This is bad design. You should use dependency inject instead!
    public BehaviorSubject<Event> getSubject() {
        return mSubject;
    }
}
