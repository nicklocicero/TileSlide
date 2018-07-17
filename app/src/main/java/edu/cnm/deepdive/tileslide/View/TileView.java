package edu.cnm.deepdive.tileslide.View;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class TileView extends AppCompatImageView {


  public TileView(Context context) {
    super(context);
  }

  public TileView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TileView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int size = Math.min(getMeasuredHeight(), getMeasuredWidth());
    setMeasuredDimension(size, size);
  }


}