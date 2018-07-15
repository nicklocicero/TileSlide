package edu.cnm.deepdive.tileslide.pojo;

import edu.cnm.deepdive.tileslide.model.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;


public class Manhattan {

  public void solve(Frame frame) {
    PriorityQueue<Frame> states = new PriorityQueue<>();
    List<String> path = new ArrayList<>();
    states.add(frame);
    while (states.size() > 0) {
      Frame state = states.poll();
      if (state.isWin()) {
        return state.getPath();
      }
    }
  }

}
