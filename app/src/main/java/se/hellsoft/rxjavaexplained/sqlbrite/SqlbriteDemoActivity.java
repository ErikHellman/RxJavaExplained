package se.hellsoft.rxjavaexplained.sqlbrite;

import static se.hellsoft.rxjavaexplained.sqlbrite.KittenProvider.KITTEN_URI;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import se.hellsoft.rxjavaexplained.R;

public class SqlbriteDemoActivity extends AppCompatActivity {
    private static final int SEARCH_MSG = 1001;

    @Bind(R.id.list_of_kittens)
    RecyclerView mListOfKittens;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private Subscription subscription;
    private SqlBrite sqlBrite;
    private KittenAdapter mKittenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_kittens_layout);
        ButterKnife.bind(this);
        mFab.setOnClickListener(this::addRandomKitten);
        sqlBrite = SqlBrite.create();
        mKittenAdapter = new KittenAdapter(this);
        mListOfKittens.setLayoutManager(new LinearLayoutManager(this));
        mListOfKittens.setAdapter(mKittenAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        BriteContentResolver resolver = sqlBrite.wrapContentProvider(getContentResolver());
        subscription = resolver.createQuery(KITTEN_URI, Kitten.COLUMNS, null, null, Kitten.COLUMN_NAME, true)
                .mapToList(Kitten::cursorToKitten)
                .subscribe(mKittenAdapter::setKittens);
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
