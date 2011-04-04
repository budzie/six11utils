package org.six11.util.tmp2;

import java.util.ArrayList;
import java.util.List;

import org.six11.util.Debug;
import static org.six11.util.Debug.num;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;
import org.six11.util.pen.Vec;
import org.six11.util.tmp2.Segment.Terminal;

import static java.lang.Math.min;
import static java.lang.Math.ceil;

@SuppressWarnings("unused")
public abstract class Segment {

  int id;
  private static int ID_COUNTER = 1;

  public static enum Type {
    Line, Curve, Unknown, EllipticalArc
  };

  List<Pt> points;
  Type type;
  Line line;
  Sequence spline;
  boolean[] terms;

  public Segment(List<Pt> points, boolean termA, boolean termB) {
    this.points = points;
    this.type = Type.Unknown;
    terms = new boolean[] {
        termA, termB
    };
    id = ID_COUNTER++;
  }

  public int getId() {
    return id;
  }

  public Type getType() {
    return type;
  }

  public Pt getP1() {
    return points.get(0);
  }

  public Pt getP2() {
    return points.get(points.size() - 1);
  }

  public Line asLine() {
    if (line == null) {
      line = new Line(getP1(), getP2());
    }
    return line;
  }

  public double length() {
    double ret = 0;
    if (type == Type.Line) {
      ret = getP1().distance(getP2());
    } else if (type == Type.Curve) {
      ret = asSpline().length();
    }
    return ret;
  }

  public double ctrlPointLength() {
    double ret = 0;
    for (int i = 0; i < points.size() - 1; i++) {
      ret += points.get(i).distance(points.get(i + 1));
    }
    return ret;
  }

  private void warn(String what) {
    Debug.out("Segment", "** warning ** " + what);
  }

  private void bug(String what) {
    Debug.out("Segment", what);
  }

  public Sequence asSpline() {
    if (spline == null) {
      double roughLength = 0;
      for (int i = 0; i < points.size() - 1; i++) {
        roughLength = roughLength + points.get(i).distance(points.get(i + 1));
      }
      int numSteps = (int) ceil(min(roughLength / 100, 10));
      spline = Functions.makeNaturalSpline(numSteps, points);
    }
    return spline;
  }

  public class Terminal {

    Pt pt;
    Vec dir;
    boolean fixed;

    private Terminal(Pt pt, Vec dir, boolean fixed) {
      this.pt = pt;
      this.dir = dir;
      this.fixed = fixed;
    }

    public Pt getPoint() {
      return pt;
    }

    public Vec getDir() {
      return dir;
    }

    public String toString() {
      return "Segment " + id + ", " + num(pt) + "/" + num(dir) + "(" + (fixed ? "fixed" : "free")
          + ")]";
    }

    public Segment getSegment() {
      return Segment.this;
    }

    public boolean isSame(Terminal near) {
      return (pt.isSameLocation(near.getPoint()) && dir.isSame(near.getDir()));
    }

    public boolean isFixed() {
      return fixed;
    }

    public Line getLine() {
      return new Line(getPoint(), getDir());
    }

    public Pt getOpposingTermPoint() {
      return pt == getP1() ? getP2() : getP1();
    }

  }

  public List<Terminal> getTerminals() {
    List<Terminal> ret = new ArrayList<Terminal>();

    if (type == Segment.Type.Line) {
      ret.add(new Terminal(getP1(), new Vec(getP2(), getP1()).getUnitVector(), !terms[0]));
    } else if (type == Segment.Type.Curve || type == Segment.Type.EllipticalArc) {
      ret.add(new Terminal(getP1(), new Vec(points.get(1), points.get(0)).getUnitVector(),
          !terms[0]));
    } else {
      warn("unknown seg type in getTerminals");
    }

    if (type == Segment.Type.Line) {
      ret.add(new Terminal(getP2(), new Vec(getP1(), getP2()).getUnitVector(), !terms[1]));
    } else if (type == Segment.Type.Curve || type == Segment.Type.EllipticalArc) {
      ret.add(new Terminal(getP2(), new Vec(points.get(points.size() - 2),
          points.get(points.size() - 1)).getUnitVector(), !terms[1]));
    } else {
      warn("unknown seg type in getTerminals");
    }

    return ret;
  }

  /**
   * If you modify the segment externally, call this so cached stuff is re-calcuated.
   */
  public void setModified() {
    spline = null;
  }

  public List<Pt> asPolyline() {
    return points;
  }

}
