package se.hellsoft.rxjavaexplained.sqlbrite;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class KittenViewHolder extends RecyclerView.ViewHolder {
  public TextView nameView;

  public KittenViewHolder(View itemView) {
    super(itemView);
    nameView = (TextView) itemView.findViewById(android.R.id.text1);
  }
}
