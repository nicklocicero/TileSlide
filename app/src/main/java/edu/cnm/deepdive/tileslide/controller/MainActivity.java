package edu.cnm.deepdive.tileslide.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import edu.cnm.deepdive.tileslide.R;
import edu.cnm.deepdive.tileslide.View.FrameAdapter;
import edu.cnm.deepdive.tileslide.model.Frame;
import edu.cnm.deepdive.tileslide.pojo.Puzzle;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

  private static int PUZZLE_SIZE = 3;

  private Frame frame;
  private FrameAdapter adapter;
  private GridView tileGrid;
  private Button reset;
  private Button newGame;
  private Button hint;
  private Puzzle puzzle;
  private String[] lastMoves = new String[2];
  private int[] currentMove;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tileGrid = findViewById(R.id.tile_grid);
    tileGrid.setNumColumns(PUZZLE_SIZE);
    tileGrid.setOnItemClickListener(this);
    newGame = findViewById(R.id.new_game);
    reset = findViewById(R.id.reset_game);
    hint = findViewById(R.id.hint);
    if (savedInstanceState != null) {
      createPuzzle();
      frame.setTilesOrder(savedInstanceState.getIntArray("tilesOrder"));
      frame.setStartOrder(savedInstanceState.getIntArray("startOrder"));
      frame.setMoves(savedInstanceState.getInt("moves"));
      adapter.notifyDataSetChanged();
    } else {
      createPuzzle();
    }
    reset.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        frame.reset();
        adapter.notifyDataSetChanged();
      }
    });
    newGame.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        createPuzzle();
      }
    });
    hint.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        puzzle = new Puzzle(frame.getTiles(), PUZZLE_SIZE, frame.getThisLastMove());
        String hint = puzzle.hint(frame.getLastMove());
        Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
    if (frame.getWin()) {
      Toast.makeText(this, "You already won.", Toast.LENGTH_SHORT).show();
    } else {
      frame.move(position / PUZZLE_SIZE, position % PUZZLE_SIZE);
      adapter.notifyDataSetChanged();
//      int last = frame.getAnimationLastMove();
//      String direction = frame.getMove(position / PUZZLE_SIZE, position % PUZZLE_SIZE);
//      frame.move(position / PUZZLE_SIZE, position % PUZZLE_SIZE);
//      ObjectAnimator animation = null;
//      if (direction.equals("left")) {
//        animation = ObjectAnimator.ofFloat(view, "translationX", -view.getWidth());
//      } else if (direction.equals("right")) {
//        animation = ObjectAnimator.ofFloat(view, "translationX", view.getWidth());
//      } else if (direction.equals("down")) {
//        animation = ObjectAnimator.ofFloat(view, "translationY", view.getHeight());
//      } else if (direction.equals("up")) {
//        animation = ObjectAnimator.ofFloat(view, "translationY", -view.getHeight());
//      }
//      if (animation != null) {
//        animation.setDuration(400);
//        animation.start();
//        animation.addListener(new AnimatorListenerAdapter() {
//          @Override
//          public void onAnimationEnd(Animator animation) {
//            frame.setAnimationLastMove(position);
//            adapter.notifyDataSetChanged();
//          }
//        });
//      }
      if (frame.getWin()) {
        Toast.makeText(this, "You won!", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void createPuzzle() {
    frame = new Frame(PUZZLE_SIZE, new Random());
    adapter = new FrameAdapter(this, frame);
    tileGrid.setAdapter(adapter);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putIntArray("tilesOrder", frame.getTilesOrder());
    savedInstanceState.putIntArray("startOrder", frame.getStartOrder());
    savedInstanceState.putInt("moves", frame.getMoves());
  }
}
