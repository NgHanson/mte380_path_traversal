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

  static char[][] gridOne =  { // y
    {'v','v','t','v','v','v'}, // 0
    {'v','t','v','v','t','a'}, // 1
    {'v','v','v','v','v','t'}, // 2
    {'t','o','v','a','v','v'}, // 3
    {'v','t','v','v','t','v'}, // 4
    {'v','v','v','t','v','v'}, // 5
  //x 0   1   2   3   4   5
  };

  // should access as selectedGrid[y][x]
  static char[][] selectedGrid = gridOne;
  static Set<Character> validDirs = new HashSet<>();

  public static void main(String[] args) {

    // SETUP
      validDirs.add('u');
      validDirs.add('d');
      validDirs.add('l');
      validDirs.add('r');

    // ------ //
    PriorityQueue<TilePath> pq = new PriorityQueue<>(new CustomComparator());

    List<Coordinate> starting = new ArrayList<>();
    starting.add(new Coordinate(3, 0, 'u'));
    pq.add(new TilePath(starting, 0));

    while (!pq.isEmpty()) {

      TilePath currTilePath = pq.poll();

      int currCost = currTilePath.totalCost;
      List<Coordinate> currList = currTilePath.path;
      Coordinate curr = currList.get(currList.size() - 1);
      char currType = selectedGrid[curr.x][curr.y];
      char currDir = curr.dir;

      //WE CAN ONLY MOVE FORWARD IN THE DIRECTION WE'RE FACING

      int forwardX = curr.x;
      int forwardY = curr.y;

      if (currDir == 'u') {
        forwardY++;
      } else if (currDir == 'l') {
        forwardX++;
      } else if (currDir == 'r') {
        forwardX--;
      }

      if (validLocation(forwardX, forwardY)) {
        if (selectedGrid[forwardY][forwardX] == 'o') {
          printPath(currList);
          break;
        } else {
          List<Coordinate> newPath = deepCopy(currList);
          newPath.add(new Coordinate(forwardX, forwardY, currDir));
          pq.add(new TilePath(newPath, currCost + 1));
        }
      }

      // WE CAN ROTATE TO ALL THE !OTHER! DIRECTIONS
      if (currType != 't') {

        for (char c : validDirs) {
          if (c != currDir) {
            List<Coordinate> newPath = deepCopy(currList);
            newPath.add(new Coordinate(forwardX, forwardY, c));
            pq.add(new TilePath(newPath, currCost + 1));
          }
        }
      }

      // ASSUME WE WILL NEVER REVERSE
      // THIS LAST CASE IS IF THERE A HAZARD IN THE CORNER AND WE HAVE TO REVERSE ... WONT HAPPEN ...

    }

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
  }

  public static boolean validLocation(int x, int y) {
    return (x < 6 && x >= 0 && y < 6 && y >= 0 && selectedGrid[y][x] != 'a');
  }
}

class TilePath {

  int totalCost;
  List<Coordinate> path;

  public TilePath(List<Coordinate> list, int cost) {
    this.path = list;
    this.totalCost = cost;
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
    return first.totalCost - second.totalCost;
  }
}
