package org.six11.util.solve;

import java.awt.Color;

import org.six11.util.pen.DrawingBuffer;
import org.six11.util.pen.DrawingBufferRoutines;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

import static org.six11.util.Debug.num;
import static java.lang.Math.toDegrees;
import static java.lang.Math.abs;

public class OrientationConstraint extends Constraint {

  public static double TOLERANCE = 0.0001;

  Pt lineA1, lineA2, lineB1, lineB2;
  double angle;

  /**
   * This constrains two lines to some angle.
   */
  public OrientationConstraint(Pt lineA1, Pt lineA2, Pt lineB1, Pt lineB2, double radians) {
    this.lineA1 = lineA1;
    this.lineA2 = lineA2;
    this.lineB1 = lineB1;
    this.lineB2 = lineB2;
    this.angle = radians;
  }

  public String getType() {
    return "Orientation";
  }

  public void accumulateCorrection() {
    double e = measureError();
    addMessage("Error is " + num(e) + " (" + num(toDegrees(e)) + " deg) Orientation: " + num(angle)
        + " (" + num(toDegrees(angle)) + ")");
    if (abs(e) > TOLERANCE) {
      rotate(lineA1, lineA2, e);
      rotate(lineB1, lineB2, -e);
//      Line lineB = new Line(lineB1, lineB2);
//      Pt midB = lineB.getMidpoint();
//      Pt rotatedB1 = Functions.rotatePointAboutPivot(lineB1, midB, -e / 2);
//      Pt rotatedB2 = Functions.rotatePointAboutPivot(lineB2, midB, -e / 2);
//      Vec vecB1 = new Vec(rotatedB1.x - lineB1.x, rotatedB1.y - lineB1.y);
//      Vec vecB2 = new Vec(rotatedB2.x - lineB2.x, rotatedB2.y - lineB2.y);
//      accumulate(lineB1, vecB1);
//      accumulate(lineB2, vecB2);
    }
  }

  private void rotate(Pt pt1, Pt pt2, double amt) {
    // three cases: 
    // both points are free = rotate about mid by (amt / 2)
    // one point free = rotate free point about pinned point by amt
    // both points are pinned = do nothing
    Line line = new Line(pt1, pt2);
    int free = 2 - countPinned(pt1, pt2);
    if (free == 2) {
      Pt pivot = line.getMidpoint();
      Pt rotated1 = Functions.rotatePointAboutPivot(pt1, pivot, amt / 2);
      Vec vec1 = new Vec(rotated1.x - pt1.x, rotated1.y - pt1.y);
      accumulate(pt1, vec1);
      Pt rotated2 = Functions.rotatePointAboutPivot(pt2, pivot, amt / 2);
      Vec vec2 = new Vec(rotated2.x - pt2.x, rotated2.y - pt2.y);
      accumulate(pt2, vec2);
    } else if (free == 1) {
      Pt pivot = isPinned(pt1) ? pt1 : pt2;
      Pt moveMe = isPinned(pt1) ? pt2 : pt1;
//      double signedAmt = isPinned(pt1) ? amt : -amt;
      Pt rotated = Functions.rotatePointAboutPivot(moveMe, pivot, amt / 2);
      Vec vec = new Vec(rotated.x - moveMe.x, rotated.y - moveMe.y);
      accumulate(moveMe, vec);
    }

  }

  public double measureError() {
    double ret = 0;
    Vec vA = new Vec(lineA1, lineA2);
    Vec vB = new Vec(lineB1, lineB2);
    double currentAngle = Functions.getSignedAngleBetween(vA, vB);
    ret = Math.signum(currentAngle) * (Math.abs(currentAngle) - angle);
    return ret;
  }

  @Override
  public void draw(DrawingBuffer buf) {
    Line lineA = new Line(lineA1, lineA2);
    Line lineB = new Line(lineB1, lineB2);
    DrawingBufferRoutines.cross(buf, lineA.getMidpoint(), 6, Color.LIGHT_GRAY);
    DrawingBufferRoutines.cross(buf, lineB.getMidpoint(), 6, Color.LIGHT_GRAY);
    DrawingBufferRoutines.line(buf, lineA, Color.CYAN, 2);
    DrawingBufferRoutines.line(buf, lineB, Color.CYAN.darker(), 2);
  }

}
