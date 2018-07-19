package edu.cnm.deepdive.tileslide.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

public class Frame implements Comparable<Frame> {

  private int size;
  private Random rng;
  private Tile[][] start;
  private Tile[][] tiles;
  private int moves;
  private boolean win = false;
  private int[] tilesOrder;
  private int[] startOrder;
  private List<Integer[]> path = new ArrayList<>();
  private int distance;
  private int lastMove;
  private int thisLastMove;
  private String test = "hi";
  private int currentMove;
  private int animationLastMove;

  private static final Map<String, String> DIRECTIONS = new HashMap(){{
      put("LEFT", "left");
      put("RIGHT", "right");
      put("UP", "up");
      put("DOWN", "down");
  }};

  public boolean getWin() {
    return win;
  }

  public Frame(int size, Random rng) {
    this.size = size;
    this.rng = rng;
    start = new Tile[size][size];
    tiles = new Tile[size][size];
    for (int i = 0; i < size * size - 1; i++) {
      tiles[i / size][i % size] = new Tile(i);
    }
    tiles[size - 1][size - 1] = null;
    start[size-1][size-1] = null;
    tilesOrder = new int[size * size];
    startOrder = new int[size * size];
    scramble();
    this.thisLastMove = -1;
    this.animationLastMove = -1;
    this.lastMove = -1;
    this.setTilesOrder(this.getTilesOrder());
    this.setStartOrder(this.getStartOrder());
  }

  public void reset() {
    copy(start, tiles);
    moves = 0;
  }

  public boolean isWin() {
    int previous = -1;
    for (Tile[] tile : tiles) {
      for (Tile tile1: tile) {
        if (tile1 == null) {
          previous++;
          continue;
        }
        if (previous >= tile1.getNumber()) {
          return false;
        }
        previous++;
      }
    }
    return true;
  }

  public void scramble() {
    shuffle();
    if (!isParityEven()) {
      swapRandomPair();
    }
    copy(tiles, start);
    moves = 0;
  }

  public Tile[][] getTiles() {
    return tiles;
  }

  public int getMoves() {
    return moves;
  }

  private int[] getBlankSpacePosition() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (tiles[i][j] == null) {
          return new int[] {i, j};
        }
      }
    }
    return new int[] {0, 0};
  };

  private boolean isMove (int row, int col) {
    return isMove(row, col, row - 1, col)
        || isMove(row, col, row, col + 1)
        || isMove(row, col, row + 1, col)
        || isMove(row, col, row, col - 1);
  }

  private boolean isMove(int fromRow, int fromCol, int toRow, int toCol) {
    return !win &&
        tiles[fromRow][fromCol] != null
        && toRow >= 0
        && toRow < size
        && toCol >= 0
        && toCol < size
        && tiles[toRow][toCol] == null;
  }

  public boolean move(int row, int col) {
    return move(row, col, row - 1, col)
        || move(row, col, row, col + 1)
        || move(row, col, row + 1, col)
        || move(row, col, row, col - 1);
  }

  private boolean move(int fromRow, int fromCol, int toRow, int toCol) {
    if (!win &&
          tiles[fromRow][fromCol] != null
              && toRow >= 0
              && toRow < size
              && toCol >= 0
              && toCol < size
              && tiles[toRow][toCol] == null
      ) {
      lastMove = tiles[fromRow][fromCol].getNumber();
      swap(tiles, fromRow, fromCol, toRow, toCol);
      win = isWin();
      return true;
    }
    return false;
  }

  private void copy(Tile[][] source, Tile[][] dest) {
    for (int row = 0; row < size; row++) {
      System.arraycopy(source[row], 0, dest[row], 0, size);
    }
  }

  private int distanceHome(int row, int col) {
    Tile tile = tiles[row][col];
    int homeRow;
    int homeCol;
    if (tile != null) {
      homeRow = tile.getNumber() / size;
      homeCol = tile.getNumber() % size;
    } else {
      homeRow = size - 1;
      homeCol = size - 1;
    }
    return Math.abs(row - homeRow) + Math.abs(col - homeCol);
  }

  private void shuffle() {
    for (int toPosition = size * size - 1; toPosition >= 0; toPosition--) {
      int toRow = toPosition / size;
      int toCol = toPosition % size;
      int fromPosition = rng.nextInt(toPosition + 1);
      if (fromPosition != toPosition) {
        int fromRow = fromPosition / size;
        int fromCol = fromPosition % size;
        swap(tiles, fromRow, fromCol, toRow, toCol);
      }
    }
//    swap(tiles, 3, 3, 3, 2);
//    swap(tiles, 3, 2, 2, 2);
  }

  private boolean isParityEven() {
    int sum = 0;
    Tile[][] work = new Tile[size][size];
    copy(tiles, work);
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (tiles[row][col] == null) {
          sum += distanceHome(row, col);
          break;
        }
      }
    }
    for (int fromRow = 0; fromRow < size; fromRow++) {
      for (int fromCol = 0; fromCol < size; fromCol++) {
        int fromPosition = fromRow * size + fromCol;
        int toPosition = (work[fromRow][fromCol] != null)
            ? work[fromRow][fromCol].getNumber() : size * size - 1;
        while (toPosition != fromPosition) {
          int toRow = toPosition / size;
          int toCol = toPosition % size;
          swap(work, fromRow, fromCol, toRow, toCol);
          sum++;
          toPosition = (work[fromRow][fromCol] != null)
              ? work[fromRow][fromCol].getNumber() : size * size - 1;
        }
      }
    }
    return (sum & 1) == 0;
  }

  private void swapRandomPair() {
    int fromPosition = rng.nextInt(size * size);
    while (tiles[fromPosition / size][fromPosition % size] == null) {
      fromPosition = rng.nextInt(size * size);
    }
    int fromRow = fromPosition / size;
    int fromCol = fromPosition % size;
    int toPosition = rng.nextInt(size * size);
    while (toPosition == fromPosition
        || tiles[toPosition / size][toPosition % size] == null) {
      toPosition = rng.nextInt(size * size);
    }
    int toRow = toPosition / size;
    int toCol = toPosition % size;
    swap(tiles, fromRow, fromCol, toRow, toCol);
  }

  private void swap(Tile[][] tiles, int fromRow, int fromCol, int toRow, int toCol) {
    Tile temp = tiles[toRow][toCol];
    tiles[toRow][toCol] = tiles[fromRow][fromCol];
    tiles[fromRow][fromCol] = temp;
  }

  public int[] getTilesOrder() {
    return getOrder(tiles, tilesOrder);
  }

  public void setTilesOrder(int[] order) {
    setOrder(tiles, order);
  }

  public int[] getStartOrder() {
    return getOrder(start, startOrder);
  }

  public void setStartOrder(int[] order) {
    setOrder(start, order);
  }

  private int[] getOrder(Tile[][] tiles, int[] order) {
    int count = 0;
    for (Tile[] tile : tiles) {
      for (Tile tile1 : tile) {
        if (tile1 == null) {
          order[count] = size * size - 1;
        } else {
          order[count] = tile1.getNumber();
        }
        count++;
      }
    }
    return order;
  }

  public void setOrder(Tile[][] tiles, int[] order) {
    for (int i = 0; i < order.length; i++) {
      if (order[i] == size * size - 1) {
        tiles[i / size][i % size] = null;
      } else {
        tiles[i / size][i % size] = new Tile(order[i]);
      }
    }
  }

  public void setMoves(int moves) {
    this.moves = moves;
  }

  public void solve() {
    PriorityQueue<Frame> states = new PriorityQueue<>();
    states.add(this);
    while (states.size() > 0) {
      Frame state = states.poll();
      if (state.isWin()) {
        setPath(state.getPath());
        break;
      }
      List<Frame> children = visit(state);
      for (int i = 0; i < children.size(); i++) {
        Frame child = children.get(i);
        int f = child.getMoves() + child.h();
        child.setDistance(f);
        states.add(child);
        Log.d("PATH", "We have " + Integer.toString(states.size()) + " states.");
      }
    }
  }

  private int g() {
    return this.path.size();
  }

  private int h() {
      int count = 0;
      for (int i = 0; i < size * size; i++) {
        if (tiles[i / size][i % size] != null) {
          int tileNo = tiles[i / size][i % size].getNumber();
          if (tileNo != i) {
            count++;
          }
        }
      }
      return count;
  }

  private List<Frame> visit(Frame frame) {
    List<Frame> children = new ArrayList<>();
    List<Integer[]> allowedMoves = getAllowedMoves();
    for (int i = 0; i < allowedMoves.size(); i++)  {
      Integer[] move = allowedMoves.get(i);
      if (tiles[move[0]][move[1]].getNumber() != frame.lastMove) {
        Frame newInstance = new Frame(size, new Random());
        newInstance.setTilesOrder(frame.getTilesOrder());
        newInstance.setStartOrder(frame.getStartOrder());
        newInstance.setMoves(frame.getMoves());
        newInstance.move(move[0], move[1]);
        newInstance.setPath(frame.getPath());
        newInstance.addToPath(new Integer[] {move[0], move[1]});
        children.add(newInstance);
      }
    }
    return children;
  };

  private List<Integer[]> getAllowedMoves() {
    List<Integer[]> allowedMoves = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        String move = getMove(i, j);
        if (!move.equals("")) {
          allowedMoves.add(new Integer[] {i, j});
        }
      }
    }
    return allowedMoves;
  };

  public String getMove (int row, int col) {
    int[] blankSpacePosition = getBlankSpacePosition();
    int blankRow = blankSpacePosition[0];
    int blankCol = blankSpacePosition[1];
    if (blankRow > 0 && row + 1 == blankRow && col == blankCol) {
      return DIRECTIONS.get("DOWN");
    } else if (blankRow < size - 1 && row - 1 == blankRow && col == blankCol) {
      return DIRECTIONS.get("UP");
    } else if (blankCol > 0 && row == blankRow && col + 1 == blankCol) {
      return DIRECTIONS.get("RIGHT");
    } else if (blankCol < size - 1 && row == blankRow && col - 1 == blankCol) {
      return DIRECTIONS.get("LEFT");
    }
    return "";
  }

  public void moveTileByValue (int tileNumber) {
    tilesOrder = getTilesOrder();
    int counter = 0;
    for (Integer i : tilesOrder) {
      if (i == tileNumber) {
        break;
      }
      counter++;
    }
    move(counter / size, counter & size);
  }

  private String move(int row, int col, boolean use) {
    String move = getMove(row, col);
    if (!move.equals("")) {
      int[] blankSpacePosition = getBlankSpacePosition();
      int blankRow = blankSpacePosition[0];
      int blankCol = blankSpacePosition[1];
      switch (move) {
        case "left":
          this.swap(tiles, blankRow, blankCol, blankRow, blankCol + 1);
          break;
        case "right":
          this.swap(tiles, blankRow, blankCol, blankRow, blankCol - 1);
          break;
        case "up":
          this.swap(tiles, blankRow, blankCol, blankRow + 1, blankCol);
          break;
        case "down":
          this.swap(tiles, blankRow, blankCol, blankRow - 1, blankCol - 1);
          break;
      }
      if (!move.equals("")) {
        lastMove = tiles[row][col].getNumber();
      }
    }
    return move;
  }

  public String move(int piece) {
    String move = getMove(piece / size, piece % size);
    if (move != null) {
      int[] blankSpacePosition = getBlankSpacePosition();
      int row = blankSpacePosition[0];
      int column = blankSpacePosition[1];
      switch (move) {
        case "left":
          swap(tiles, row, column, row, column + 1);
          break;
        case "right":
          swap(tiles, row, column, row, column - 1);
          break;
        case "up":
          swap(tiles, row, column, row + 1, column);
          break;
        case "down":
          swap(tiles, row, column, row - 1, column);
          break;
      }
      if (move != null) {
        lastMove = piece;
      }
    }
    return move;
  }

  public void move(String move, int piece) {
    if (move != null) {
      int[] blankSpacePosition = getBlankSpacePosition();
      int row = blankSpacePosition[0];
      int column = blankSpacePosition[1];
      switch (move) {
        case "move left tile":
          swap(tiles, row, column, row, column - 1);
          break;
        case "move right tile":
          swap(tiles, row, column, row, column + 1);
          break;
        case "move bottom tile":
          swap(tiles, row, column, row + 1, column);
          break;
        case "move top tile":
          swap(tiles, row, column, row - 1, column);
          break;
      }
//      if (move != null) {
//        lastMove = piece;
//      }
    }
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public List<Integer[]> getPath() {
    return path;
  }

  public void setPath(List<Integer[]> path) {
    Collections.copy(path, this.path);
  }

  public void addToPath(Integer[] direction) {
    this.path.add(direction);
  }

  @Override
  public int compareTo(Frame otherFrame) {
    return getDistance() - otherFrame.getDistance();
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getLastMove() {
    return lastMove;
  }

  public int getThisLastMove() {
    return thisLastMove;
  }

  public int getCurrentMove() {
    return currentMove;
  }

  public void setCurrentMove(int currentMove) {
    this.currentMove = currentMove;
  }

  public int getAnimationLastMove() {
    return animationLastMove;
  }

  public void setAnimationLastMove(int animationLastMove) {
    this.animationLastMove = animationLastMove;
  }

  public int getTileAt(int value) {
    int count = 0;
    for (Integer i : getTilesOrder()) {
      if (i == value) {
        return count;
      }
      count++;
    }
    return count;
  }

  public interface OnMoveListener {

  }

  //  private static class SolvePuzzle extends AsyncTask<Frame, Void, List<Integer[]>> {
//
//    @Override
//    protected List<Integer[]> doInBackground(Frame... frames) {
//      PriorityQueue<Frame> states = new PriorityQueue<>();
//      states.add(frames[0]);
//      while (states.size() > 0) {
//        Frame state = states.poll();
//        if (state.isWin()) {
//          return state.getPath();
//        }
//        List<Frame> children = state.visit(state);
//        for (int i = 0; i < children.size(); i++) {
//          Frame child = children.get(i);
//          int f = child.g() + child.h();
//          child.setDistance(f);
//          states.add(child);
//        }
//      }
//      return null;
//    }
//
//    @Override
//    protected void onPostExecute(List<Integer[]> result) {
//      super.onPostExecute(result);
//    }
//  }

}

  //  public class FrameComparator implements Comparator<Frame> {
//
//    public int compare( Frame x, Frame y ) {
//      return x.getDistance() - y.getDistance();
//    }
//
//  }
