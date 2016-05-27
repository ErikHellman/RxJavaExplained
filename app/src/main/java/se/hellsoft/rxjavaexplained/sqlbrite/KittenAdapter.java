package se.hellsoft.rxjavaexplained.sqlbrite;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

public class KittenAdapter extends RecyclerView.Adapter<KittenViewHolder> {
  private Activity activity;
  private List<Kitten> kittens = new LinkedList<>();

  public KittenAdapter(Activity activity) {
    this.activity = activity;
  }

  @Override
  public KittenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new KittenViewHolder(View.inflate(activity, android.R.layout.simple_list_item_1, null));
  }

  @Override
  public void onBindViewHolder(KittenViewHolder holder, int position) {
    holder.nameView.setText(kittens.get(position).name);
  }

  @Override
  public int getItemCount() {
    return kittens != null ? kittens.size() : 0;
  }

  public void addKitten(Kitten kitten) {
    kittens.add(kitten);
    ;
    notifyItemInserted(kittens.size() - 1);
  }

  public void setKittens(List<Kitten> kittens) {
    this.kittens = kittens;
    notifyDataSetChanged();
  }
}
