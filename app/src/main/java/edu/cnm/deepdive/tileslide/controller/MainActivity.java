package edu.cnm.deepdive.tileslide.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import edu.cnm.deepdive.tileslide.R;
import edu.cnm.deepdive.tileslide.View.FrameAdapter;
import edu.cnm.deepdive.tileslide.model.Frame;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

  private static int PUZZLE_SIZE = 4;

  private Frame frame;
  private FrameAdapter adapter;
  private GridView tileGrid;
  private Button reset;
  private Toast toast;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tileGrid = findViewById(R.id.tile_grid);
    tileGrid.setNumColumns(PUZZLE_SIZE);
    tileGrid.setOnItemClickListener(this);
    reset = findViewById(R.id.reset);
    toast = new Toast(this);
    if (savedInstanceState != null) {
      createPuzzle();
      frame.setTilesOrder(savedInstanceState.getIntArray("tilesOrder"));
      adapter.notifyDataSetChanged();
    } else {
      createPuzzle();
    }
    reset.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        createPuzzle();
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (frame.getWin()) {
      Toast.makeText(this, "You already won.", Toast.LENGTH_SHORT).show();
    } else {
      frame.move(position / PUZZLE_SIZE, position % PUZZLE_SIZE);
      adapter.notifyDataSetChanged();
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
  public void onStart() {
    super.onStart();
  }
  @Override
  public void onResume() {
    super.onResume();
  }
  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putIntArray("tilesOrder", frame.getTilesOrder());
  }

  @Override
  public void onStop() {
    super.onStop();
  }
  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
