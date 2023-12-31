package utility;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class SpacesDecoration extends RecyclerView.ItemDecoration {
  private int space;

  public SpacesDecoration(int space) {
    this.space = space;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

    outRect.left = space;
    outRect.right = space;
    outRect.bottom = space;
    outRect.top = space;


  }
}