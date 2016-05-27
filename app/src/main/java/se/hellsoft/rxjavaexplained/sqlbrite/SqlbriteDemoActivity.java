package se.hellsoft.rxjavaexplained.sqlbrite;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Random;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.hellsoft.rxjavaexplained.R;

import static se.hellsoft.rxjavaexplained.sqlbrite.KittenProvider.KITTEN_URI;

public class SqlbriteDemoActivity extends AppCompatActivity {
  private static final int SEARCH_MSG = 1001;

  private RecyclerView listOfKittens;
  private FloatingActionButton floatingActionButton;

  private Subscription subscription;
  private SqlBrite sqlBrite;
  private KittenAdapter kittenAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_of_kittens_layout);
    listOfKittens = (RecyclerView) findViewById(R.id.list_of_kittens);
    floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    floatingActionButton.setOnClickListener(this::addRandomKitten);
    sqlBrite = SqlBrite.create();
    kittenAdapter = new KittenAdapter(this);
    listOfKittens.setLayoutManager(new LinearLayoutManager(this));
    listOfKittens.setAdapter(kittenAdapter);
  }

  @Override
  protected void onResume() {
    super.onResume();

    BriteContentResolver resolver = sqlBrite.wrapContentProvider(getContentResolver(), Schedulers.io());
    subscription = resolver.createQuery(KITTEN_URI, Kitten.COLUMNS, null, null, Kitten.COLUMN_NAME, true)
        .mapToList(Kitten::cursorToKitten)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(kittenAdapter::setKittens);
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  private void addRandomKitten(View view) {
    Random random = new Random();
    ContentValues values = new ContentValues();
    values.put(Kitten.COLUMN_NAME, Kitten.CAT_NAMES[random.nextInt(Kitten.CAT_NAMES.length)]);
    values.put(Kitten.COLUMN_DESCRIPTION, "A very nice kitten!");
    getContentResolver().insert(KITTEN_URI, values);
  }

}
