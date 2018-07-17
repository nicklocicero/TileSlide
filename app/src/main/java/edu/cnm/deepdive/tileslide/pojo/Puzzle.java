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

  public Puzzle() { }

  public Puzzle(Tile[][] tiles, int size, int lastMove) {
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
      this.lastMove = lastMove;
    }
    this.path = new LinkedList<>();
    this.lastMove = -1;
    this.distance = 0;
  }

  private int[] getBlankSpacePosition() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (board[i][j] == size * size - 1) {
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
    } else if (column < size - 1 && piece == board[row][column+1]) {
      return Direction[0];
    }
    return null;
  };

  private String getPlayerMove(int piece) {
    int[] blankSpacePosition = this.getBlankSpacePosition();
    int row = blankSpacePosition[0];
    int column = blankSpacePosition[1];
    if (row > 0 && piece == board[row-1][column]) {
      return "move top tile";
    } else if (row < size - 1 && piece == board[row+1][column]) {
      return "move bottom tile";
    } else if (column > 0 && piece == board[row][column-1]) {
      return "move left tile";
    } else if (column < size - 1 && piece == board[row][column+1]) {
      return "move right tile";
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
        int originalRow = piece / size;
        int originalCol = piece % size;
        if (i != originalRow || j != originalCol) {
          return false;
        }
      }
    }
    return true;
  }

  private Puzzle getCopy(Puzzle puzzle) {
    Puzzle newPuzzle = new Puzzle();
    newPuzzle.setSize(puzzle.getSize());
    int[][] board = puzzle.getBoard();
    int[][] newBoard = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        newBoard[i][j] = board[i][j];
      }
    }
    newPuzzle.setBoard(newBoard);
    List<Integer> oldPath = puzzle.getPath();
    List<Integer> newPath = new LinkedList<>();
    for (Integer i : oldPath) {
      newPath.add(i);
    }
    newPuzzle.setPath(newPath);
    newPuzzle.setLastMove(puzzle.getLastMove());
    return newPuzzle;
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
        Puzzle newInstance = getCopy(this);
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
      if (state.isGoalState() || states.size() > 10000) {
        while (state.path.get(0) == lastMove) {
          state = states.poll();
        }
        return state.path;
      }
      List<Puzzle> children = state.visit();
      for (int i = 0; i < children.size(); i++) {
        Puzzle child = children.get(i);
        int f = child.g() + child.getManhattanDistance();
        child.setDistance(f);
        states.add(child);
      }
    }
    return new LinkedList<>();
  }

  public Integer solveForMove(Integer lastMove) {
    PriorityQueue<Puzzle> states = new PriorityQueue<>();
    path = new LinkedList<>();
    states.add(this);
    while (states.size() > 0) {
      Puzzle state = states.poll();
      if (state.isGoalState() || states.size() > 10000) {
        while (state.path.get(0) == lastMove) {
          state = states.poll();
        }
        return state.path.get(0);
      }
      List<Puzzle> children = state.visit();
      for (int i = 0; i < children.size(); i++) {
        Puzzle child = children.get(i);
        int f = child.g() + child.getManhattanDistance();
        child.setDistance(f);
        states.add(child);
      }
    }
    return path.get(0);
  }

  public String hint(Integer lastMove) {
    Integer piece = solveForMove(lastMove);
    return getPlayerMove(piece);
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

  public int[][] getBoard() {
    return board;
  }

  public void setBoard(int[][] board) {
    this.board = board;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public List<Integer> getPath() {
    return path;
  }

  public void setPath(List<Integer> path) {
    this.path = path;
  }

  public int getLastMove() {
    return lastMove;
  }

  public void setLastMove(int lastMove) {
    this.lastMove = lastMove;
  }
}