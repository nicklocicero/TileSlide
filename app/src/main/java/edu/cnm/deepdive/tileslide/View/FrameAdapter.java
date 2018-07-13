package edu.cnm.deepdive.tileslide.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import edu.cnm.deepdive.tileslide.R;
import edu.cnm.deepdive.tileslide.model.Frame;
import edu.cnm.deepdive.tileslide.model.Tile;

public class FrameAdapter extends ArrayAdapter<Tile> {

  private int size;
  private Bitmap[] tileImages;
  private Bitmap noTileImage;
  private Frame frame;
  private Tile[] tiles;

  public FrameAdapter(@NonNull Context context, @NonNull Frame frame) {
    super(context, R.layout.tile_item);
    this.frame = frame;
    size = frame.getTiles().length;
    tiles = new Tile[size * size];
    copyModelTiles();
    addAll(tiles);
    sliceBitmap();
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.tile_item, null, false);
    }
    Tile tile = getItem(position);
    TileView tileView = convertView.findViewById(R.id.tile_image);
    Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.grid_item_anim);
    tileView.setAnimation(animation);
    if (tile != null) {
      tileView.setImageBitmap(tileImages[tile.getNumber()]);
    } else {
      tileView.setImageBitmap(noTileImage);
    }
    return convertView;
  }

  @Override
  public void notifyDataSetChanged() {
    copyModelTiles();
    setNotifyOnChange(false);
    // TODO Possibly modify this if we want to add animation.

    clear();
    addAll(tiles);
    super.notifyDataSetChanged();
  }

  private void copyModelTiles() {
    Tile[][] source = frame.getTiles();
    for (int row = 0; row < size; row++) {
      System.arraycopy(source[row], 0, tiles, row * size, size);

    }
  }

  private void sliceBitmap() {
    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.android_robot_circle);
    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
    tileImages = new Bitmap[size * size];
    int imageWidth = bitmap.getWidth();
    int imageHeight = bitmap.getHeight();
    for (int i = 0; i < tileImages.length; i++) {
      int row = i / size;
      int col = i % size;
      tileImages[i] = bitmap.createBitmap(
          bitmap, col * imageWidth / size,
          row * imageHeight / size,
          imageWidth / size,
          imageHeight / size);
    }
    noTileImage = Bitmap.createBitmap(tileImages[tileImages.length - 1]);
    noTileImage.eraseColor(ContextCompat.getColor(getContext(), R.color.puzzleBackground));
  }



}
