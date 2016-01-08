package se.hellsoft.rxjavaexplained.sqlbrite;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

public class KittenAdapter extends RecyclerView.Adapter<KittenViewHolder> {
  private Activity mmActivity;
  private List<Kitten> mKittens = new LinkedList<>();

  public KittenAdapter(Activity activity) {
    mmActivity = activity;
  }

  @Override
  public KittenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new KittenViewHolder(View.inflate(mmActivity, android.R.layout.simple_list_item_1, null));
  }

  @Override
  public void onBindViewHolder(KittenViewHolder holder, int position) {
    holder.nameView.setText(mKittens.get(position).name);
  }

  @Override
  public int getItemCount() {
    return mKittens != null ? mKittens.size() : 0;
  }

  public void addKitten(Kitten kitten) {
    mKittens.add(kitten);
    ;
    notifyItemInserted(mKittens.size() - 1);
  }

  public void setKittens(List<Kitten> kittens) {
    mKittens = kittens;
    notifyDataSetChanged();
  }
}
