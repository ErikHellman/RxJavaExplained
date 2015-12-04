package se.hellsoft.rxjavaexplained.instantsearch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import se.hellsoft.rxjavaexplained.R;

public class InstantSearchDemo extends AppCompatActivity {
    private static final String TAG = "InstantSearchDemo";
    @Bind(R.id.search_input)
    EditText searchInput;
    @Bind(R.id.search_result)
    RecyclerView searchResult;
    private Subscription searchSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instant_search_demo);
        ButterKnife.bind(this);

        SearchResultAdapter adapter = new SearchResultAdapter();
        searchResult.setLayoutManager(new LinearLayoutManager(this));
        searchResult.setAdapter(adapter);
        searchSubscription = RxTextView.afterTextChangeEvents(searchInput)
                // Convert the event to a String
                .map(textChangeEvent -> textChangeEvent.editable().toString())
                        // If we get multiple events within 200ms, just emit the last one
                .debounce(200, MILLISECONDS)
                        // "Convert" the query string to a search result
                .switchMap(this::searchNames)
                        // Switch back to the main thread
                .observeOn(AndroidSchedulers.mainThread())
                        // Set the result on our adapter
                .subscribe(adapter::setSearchResult);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!searchSubscription.isUnsubscribed()) {
            searchSubscription.unsubscribe();
        }
    }

    private Observable<List<String>> searchNames(String query) {
        Log.d(TAG, "searchNames: Search for " + query);
        if (query == null || query.length() == 0) {
            return Observable.just(new LinkedList<>());
        }
        BufferedReader reader = null;
        LinkedList<String> result;
        try {
            InputStream inputStream = getResources()
                    .openRawResource(R.raw.unique_random_strings);
            InputStreamReader inputStreamReader
                    = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            String line;
            result = new LinkedList<>();
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(query.toLowerCase())) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            return Observable.error(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        Collections.sort(result);
        Log.d(TAG, "searchNames: Found " + result.size() + " hits!");
        return Observable.just(result);
    }

    private class SearchResultAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {
        private List<String> mResult;

        @Override
        public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = View.inflate(InstantSearchDemo.this,
                    android.R.layout.simple_list_item_1, null);
            return new SearchResultViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(SearchResultViewHolder holder, int position) {
            holder.textView.setText(mResult.get(position));
        }

        @Override
        public int getItemCount() {
            return mResult != null ? mResult.size() : 0;
        }

        public void setSearchResult(List<String> result) {
            mResult = result;
            notifyDataSetChanged();
        }
    }

    private class SearchResultViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
