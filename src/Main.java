import java.util.*;

public class Main {

  /*
  v - visited
  a - avoid
  t - cannot turn
  o - objective
   */

  /*
    VIEW DIRECTIONS AS

           down
            ^
            |
right < _ _ |_ _ > left
            |
            |
            v
            up
   */

  static char[][] grid1 =  { // y
    {'v','v','t','v','v','v'}, // 0
    {'v','t','v','v','t','a'}, // 1
    {'v','v','v','v','v','t'}, // 2
    {'t','o','v','a','v','v'}, // 3
    {'v','t','v','v','t','v'}, // 4
    {'v','v','v','t','v','v'}, // 5
  //x 0   1   2   3   4   5
  };
  static Coordinate obj1 = new Coordinate(1, 3, '-');

  static char[][] grid2 =  { // y
    {'v','v','t','v','v','v'}, // 0
    {'v','t','v','v','t','a'}, // 1
    {'v','v','v','v','v','t'}, // 2
    {'t','v','v','a','v','v'}, // 3
    {'v','t','v','v','t','v'}, // 4
    {'v','v','v','t','v','v'}, // 5
  //x 0   1   2   3   4   5
  };
  static Coordinate obj2 = new Coordinate(5, 5, '-');

  static char[][] grid3 =  { // y
    {'v','v','t','v','v','v'}, // 0
    {'v','t','v','v','t','a'}, // 1
    {'v','v','v','v','v','t'}, // 2
    {'t','v','v','a','v','v'}, // 3
    {'v','t','v','v','t','v'}, // 4
    {'v','v','v','t','v','v'}, // 5
  //x 0   1   2   3   4   5
  };
  static Coordinate obj3 = new Coordinate(0, 4, '-');

  // ------ TESTING GRID SETUPS -------------------------//
  static int breadthCount = 0;

  // should access as selectedGrid[y][x]
  static char[][] selectedGrid = grid3;
  static Coordinate selectedObjective = obj3;

  static Coordinate startLocation = new Coordinate(3, 0, 'u');

  static char[] validDirs = {'u', 'd', 'r', 'l'};

  // should access as minCost[y][x][dir]
  // dir order is same as validDirs
  static int[][][] minCost = new int[6][6][4];

  public static void main(String[] args) {

    // SETUP STEPS
    for (int i = 0; i < minCost.length; i++) {
      for (int j = 0; j < minCost[0].length; j++) {
        for (int k = 0; k < minCost[0][0].length; k++) {
          minCost[i][j][k] = Integer.MAX_VALUE; // SHOULD BE HIGH ENOUGH
        }
      }
    }

    PriorityQueue<TilePath> pq = new PriorityQueue<>(new CustomComparator());

    List<Coordinate> starting = new ArrayList<>();
    starting.add(startLocation);
    pq.add(new TilePath(starting, 0, euclideanDist(startLocation.x, startLocation.y)));
    minCost[startLocation.y][startLocation.x][dirValue(startLocation.dir)] = 0;
    // ------ //

    while (!pq.isEmpty()) {

      TilePath currTilePath = pq.poll();

      int currPathCost = currTilePath.pathCost;
      List<Coordinate> currList = currTilePath.path;
      Coordinate curr = currList.get(currList.size() - 1);
      char currDir = curr.dir;

      System.out.println("CURR POS - X: " + curr.x + " Y: " + curr.y + " DIR: " + currDir +
        " Curr Path Cost: " + currPathCost + " PQ Size: " + pq.size() );
      System.out.println();
      //WE CAN ONLY MOVE FORWARD IN THE DIRECTION WE'RE FACING

      int forwardX = curr.x;
      int forwardY = curr.y;

      if (currDir == 'u') {
        forwardY++;
      } else if (currDir == 'l') {
        forwardX++;
      } else if (currDir == 'r') {
        forwardX--;
      } else if (currDir == 'd') {
        forwardY--;
      }

      if (validLocation(forwardX, forwardY)) {
        if (forwardY == selectedObjective.y && forwardX == selectedObjective.x) {
          printPath(currList);
          return;
        } else {
          int updatedPathCost = currPathCost + 1;
          if (updatedPathCost < minCost[forwardY][forwardX][dirValue(currDir)] ) {
            double distCost = euclideanDist(forwardX, forwardY);
            minCost[forwardY][forwardX][dirValue(currDir)] = updatedPathCost;
            Coordinate newCoord = new Coordinate(forwardX, forwardY, currDir);
            List<Coordinate> newPath = deepCopy(currList);
            newPath.add(newCoord);
            pq.add(new TilePath(newPath, updatedPathCost, distCost));
            breadthCount++;
          }
        }
      }

      // WE CAN ROTATE TO ALL THE !OTHER! DIRECTIONS
      // DO NOT GO INTO ANY OF THE HAZARDS AT ALL FOR NOW
      //if (currType != 't') {

        for (char c : validDirs) {
          if (c != currDir) {
            int updatedPathCost = currPathCost + 1;
            if (updatedPathCost < minCost[curr.y][curr.x][dirValue(c)] ) {
              double distCost = euclideanDist(curr.x, curr.y);
              minCost[curr.y][curr.x][dirValue(c)] = updatedPathCost;
              List<Coordinate> newPath = deepCopy(currList);
              newPath.add(new Coordinate(curr.x, curr.y, c));
              pq.add(new TilePath(newPath, updatedPathCost, distCost));
              breadthCount++;
            }
          }
        }
      //}

      // ASSUME WE WILL NEVER REVERSE
      // THIS LAST CASE IS IF THERE A HAZARD IN THE CORNER AND WE HAVE TO REVERSE ... WONT HAPPEN ...

    }

    System.out.println("NO PATH FOUND");
  }

  public static List<Coordinate> deepCopy(List<Coordinate> list) {
    List<Coordinate> newList = new ArrayList<>();
    for (Coordinate c : list) {
      newList.add(c);
    }
    return newList;
  }

  public static void printPath(List<Coordinate> list) {
    for (Coordinate c : list) {
      System.out.println("X: " + c.x + " Y: " + c.y + " DIR: " + c.dir);
    }
    System.out.println("BREADTH COUNT: " + breadthCount);
  }

  public static boolean validLocation(int x, int y) {
    return (x < 6 && x >= 0 &&
            y < 6 && y >= 0 &&
            selectedGrid[y][x] != 'a' &&
            selectedGrid[y][x] != 't');
  }

  public static int dirValue(char c) {

    if (c == 'u') {
      return 0;
    } else if(c == 'd') {
      return 1;
    } else if (c == 'r') {
      return 2;
    } else if (c == 'l') {
      return 3;
    } else {
      return -1;
    }
  }

  public static double euclideanDist(int x, int y) {
    return Math.pow( Math.pow(x-selectedObjective.x,2) + Math.pow(y-selectedObjective.y, 2),0.5);
  }
}

class TilePath {

  int pathCost;
  double distCost;
  List<Coordinate> path;

  public TilePath(List<Coordinate> list, int pathCost, double distCost) {
    this.path = list;
    this.pathCost = pathCost;
    this.distCost = distCost;
  }
}

class Coordinate {
  int x;
  int y;
  char dir; // u - up, d - down, l - left, r - right
  public Coordinate(int x, int y, char dir) {
    this.x = x;
    this.y = y;
    this.dir = dir;
  }
}

class CustomComparator implements Comparator<TilePath> {
  @Override
  public int compare(TilePath first, TilePath second) {
    //return first.pathCost - second.pathCost; //WITHOUT HEURISTIC
    return (int) (first.distCost - second.distCost);
  }
}
