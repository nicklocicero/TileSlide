package edu.cnm.deepdive.tileslide.View;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import edu.cnm.deepdive.tileslide.R;
import edu.cnm.deepdive.tileslide.model.Frame;
import edu.cnm.deepdive.tileslide.model.Tile;
import java.util.Random;

public class FrameAdapter extends ArrayAdapter<Tile> {

  private int size;
  private Bitmap[] tileImages;
  private Bitmap noTileImage;
  private Bitmap tileImage;
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
    if (tile != null) {
      tileView.setImageBitmap(tileImages[tile.getNumber()]);
    } else if (frame.isWin()) {
      tileView.setImageBitmap(tileImage);
    } else {
      tileView.setImageBitmap(noTileImage);
    }
    return convertView;
  }

  @Override
  public void notifyDataSetChanged() {


    // TODO Possibly modify this if we want to add animation.
//    Tile tile = getItem(frame.getLastMove());
//    TileVi
//    ObjectAnimator moveX = ObjectAnimator.ofFloat(tileImages, "x", 1);
//    ObjectAnimator moveY = ObjectAnimator.ofFloat(tileImages, "y", 1 );
//    AnimatorSet as = new AnimatorSet();
//    as.playTogether(moveX, moveY);
//    as.start();
//    int currentMove = frame.getCurrentMove();
//    String direction = frame.getMove(currentMove/size, currentMove % size);
//    Path path = new Path();
//    path.moveTo();
////    ValueAnimator pathAnimator = ObjectAnimator.ofFloat(getView(currentMove) , "x", "y", path);
//    if (frame.getAnimationLastMove() != -1) {
//      frame.move(frame.getAnimationLastMove() / size, frame.getAnimationLastMove() % size);
//    }
    copyModelTiles();
    setNotifyOnChange(false);
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
    Random rng = new Random();
    int[] res = {R.drawable.android_robot_circle, R.drawable.city, R.drawable.city_abstract, R.drawable.city_new_york,
                  R.drawable.corey_archangel, R.drawable.dataset_card, R.drawable.forest, R.drawable.forest_painting,
                  R.drawable.gradient, R.drawable.gradient_one, R.drawable.gradient_two, R.drawable.gradient_three,
                  R.drawable.gradient_four, R.drawable.gradient_five, R.drawable.jazz_club, R.drawable.kaleidoscope_one,
                  R.drawable.kaleidoscope_two, R.drawable.kaleidoscope_three, R.drawable.lavendar, R.drawable.lion,
                  R.drawable.plane, R.drawable.plane, R.drawable.wolf_cub, R.drawable.rain_forest};
    Drawable drawable = ContextCompat.getDrawable(getContext(), res[rng.nextInt(res.length)]);
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
    tileImage = Bitmap.createBitmap(tileImages[tileImages.length - 1]);
    noTileImage = Bitmap.createBitmap(tileImages[tileImages.length - 1]);
    noTileImage.eraseColor(ContextCompat.getColor(getContext(), R.color.puzzleBackground));
    noTileImage.setHasAlpha(true);
  }

  public Tile[] getTiles() {
    return tiles;
  }


}
