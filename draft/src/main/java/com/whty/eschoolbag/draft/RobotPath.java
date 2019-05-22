package com.whty.eschoolbag.draft;

import android.graphics.Path;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianqs on 2018/4/4.
 */

public class RobotPath extends Path implements Serializable {
    private static final long serialVersionUID = 1L;
    private int strokeWidth;
    private String color;
    private List<RobotPoint> points = new ArrayList<>();
    private ArrayList<PathAction> actions = new ArrayList<PathAction>();


    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<RobotPoint> getPoints() {
        return points;
    }

    public void setPoints(List<RobotPoint> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RobotPath)) {
            return false;
        }

        RobotPath o = (RobotPath) obj;
        if (this.strokeWidth == o.getStrokeWidth()
                && this.color.equals(o.getColor())
                && this.points.equals(o.getPoints())) {
            return true;
        }

        return false;
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        drawThisPath();
    }

    private void drawThisPath() {
        for (PathAction p : actions) {
            if (p.getType().equals(PathAction.PathActionType.MOVE_TO)) {
                super.moveTo(p.getX1(), p.getY1());
            } else if (p.getType().equals(PathAction.PathActionType.QUAD_TO)) {
                super.quadTo(p.getX1(), p.getY1(), p.getX2(), p.getY2());
            } else if (p.getType().equals(PathAction.PathActionType.OFFSET)) {
                super.offset(p.getX1(), p.getY1());
            }
        }
    }

    @Override
    public void moveTo(float x, float y) {
        actions.add(new ActionMove(x, y));
        super.moveTo(x, y);
        points.add(new RobotPoint(x, y));
    }


    @Override
    public void quadTo(float x1, float y1, float x2, float y2) {
        actions.add(new ActionQuad(x1, y1, x2, y2));
        super.quadTo(x1, y1, x2, y2);
        points.add(new RobotPoint(x2, y2));
    }

    @Override
    public void offset(float dx, float dy) {
        actions.add(new ActionOffset(dx, dy));
        super.offset(dx, dy);
    }


    public class RobotPoint implements Serializable {
        private static final long serialVersionUID = 1L;
        public float x;
        public float y;

        public RobotPoint() {
        }

        public RobotPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof RobotPoint)) {
                return false;
            }

            RobotPoint o = (RobotPoint) obj;
            if (this.x == o.x && this.y == o.y) {
                return true;
            }

            return false;
        }
    }

    public interface PathAction {
        public enum PathActionType {
            MOVE_TO, QUAD_TO, OFFSET
        }

        ;

        public PathActionType getType();

        public float getX1();

        public float getY1();

        public float getX2();

        public float getY2();
    }

    public class ActionMove implements PathAction, Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private float x, y;

        public ActionMove(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.MOVE_TO;
        }

        @Override
        public float getX1() {
            return x;
        }

        @Override
        public float getY1() {
            return y;
        }

        @Override
        public float getX2() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public float getY2() {
            // TODO Auto-generated method stub
            return 0;
        }

    }

    public class ActionQuad implements PathAction, Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private float x1, y1;
        private float x2, y2;

        public ActionQuad(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.QUAD_TO;
        }

        @Override
        public float getX1() {
            return x1;
        }

        @Override
        public float getY1() {
            return y1;
        }

        @Override
        public float getX2() {
            // TODO Auto-generated method stub
            return x2;
        }

        @Override
        public float getY2() {
            // TODO Auto-generated method stub
            return y2;
        }

    }

    public class ActionOffset implements PathAction, Serializable {

        private float x, y;
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public ActionOffset(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public PathActionType getType() {
            // TODO Auto-generated method stub
            return PathActionType.OFFSET;
        }

        @Override
        public float getX1() {
            // TODO Auto-generated method stub
            return x;
        }

        @Override
        public float getY1() {
            // TODO Auto-generated method stub
            return y;
        }

        @Override
        public float getX2() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public float getY2() {
            // TODO Auto-generated method stub
            return 0;
        }

    }


}
