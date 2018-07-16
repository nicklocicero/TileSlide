package edu.cnm.deepdive.tileslide.pojo;

import edu.cnm.deepdive.tileslide.model.Tile;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Puzzle implements Comparable<Puzzle> {

  int[][] board;
  int size;
  List<Integer> path;
  int lastMove;
  int distance;

  String[] Direction = {"left", "right", "up", "down"};

  public Puzzle(Tile[][] tiles, int size) {
    this.size = size;
    this.board = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (tiles[i][j] == null) {
          board[i][j] = size * size - 1;
        } else {
          board[i][j] = tiles[i][j].getNumber();
        }
      }
    }
    this.path = new LinkedList<>();
    this.lastMove = -1;
    this.distance = 0;
  }

  Puzzle(int[][] board, int size, int lastMove, List<Integer> path) {
    this.size = size;
    this.board = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        this.board[i][j] = board[i][j];
      }
    }
    this.path = new LinkedList<>();
    for (Integer move : path) {
      path.add(move);
    }
    this.lastMove = lastMove;
  }

  private int[] getBlankSpacePosition() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (board[i][j] == 0) {
          return new int[] {i, j};
        }
      }
    }
    return new int[] {0, 0};
  }

  private void swap(int fromRow, int fromCol, int toRow, int toCol) {
    int temp = board[toRow][toCol];
    board[toRow][toCol] = board[fromRow][fromCol];
    board[fromRow][fromCol] = temp;
  }

  private String getMove(int piece) {
    int[] blankSpacePosition = this.getBlankSpacePosition();
    int row = blankSpacePosition[0];
    int column = blankSpacePosition[1];
    if (row > 0 && piece == board[row-1][column]) {
      return Direction[3];
    } else if (row < size - 1 && piece == board[row+1][column]) {
      return Direction[2];
    } else if (column > 0 && piece == board[row][column-1]) {
      return Direction[1];
    } else if (column < size - 1 && piece == this.board[row][column+1]) {
      return Direction[0];
    }
    return null;
  };

  private String move(int piece) {
    String move = getMove(piece);
    if (move != null) {
      int[] blankSpacePosition = this.getBlankSpacePosition();
      int row = blankSpacePosition[0];
      int column = blankSpacePosition[1];
      switch (move) {
        case "left":
          swap(row, column, row, column + 1);
          break;
        case "right":
          swap(row, column, row, column - 1);
          break;
        case "up":
          swap(row, column, row + 1, column);
          break;
        case "down":
          swap(row, column, row - 1, column);
          break;
      }
      if (move != null) {
        lastMove = piece;
      }
    }
    return move;
  }

  private boolean isGoalState() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int piece = board[i][j];
        if (piece != size * size - 1) {
          int originalRow = piece / size;
          int originalCol = piece % size;
          if (i != originalRow && j != originalCol) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private Puzzle getCopy() {
    return new Puzzle(board, size, lastMove, path);
  }

  private List<Integer> getAllowedMoves() {
    List<Integer> allowedMoves = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int piece = board[i][j];
        if (getMove(piece) != null) {
          allowedMoves.add(piece);
        }
      }
    }
    return allowedMoves;
  }

  private List<Puzzle> visit() {
    List<Puzzle> children = new ArrayList<>();
    List<Integer> allowedMoves = getAllowedMoves();
    for (int i = 0; i < allowedMoves.size(); i++)  {
      Integer move = allowedMoves.get(i);
      if (move != lastMove) {
        Puzzle newInstance = getCopy();
        newInstance.move(move);
        newInstance.path.add(move);
        children.add(newInstance);
      }
    }
    return children;
  };

  private int g() {
    return path.size();
  }

  private int getManhattanDistance() {
    int distance = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int piece = board[i][j];
        if (piece != size * size - 1) {
          int originalRow = piece / size;
          int originalCol = piece % size;
          distance += Math.abs(i - originalRow) + Math.abs(j - originalCol);
        }
      }
    }
    return distance;
  }

  public List<Integer> solve() {
    PriorityQueue<Puzzle> states = new PriorityQueue<>();
    path = new LinkedList<>();
    states.add(this);
    while (states.size() > 0) {
      Puzzle state = states.poll();
      if (state.isGoalState()) {
        return state.path;
      }
      List<Puzzle> children = state.visit();
      for (int i = 0; i < children.size(); i++) {
        Puzzle child = children.get(i);
        int f = child.g() + child.getManhattanDistance();
        states.add(child);
      }
    }
    return new LinkedList<>();
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  @Override
  public int compareTo(Puzzle otherPuzzle) {
    return getDistance() - otherPuzzle.getDistance();
  }

}