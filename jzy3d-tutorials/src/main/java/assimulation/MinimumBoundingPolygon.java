package assimulation;

/**
 * Created by root on 16-12-19.
 */

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * <p>
 * <b>最小（凸）包围边界查找</b>
 * <p>
 * <pre>
 * 最小（凸）包围边界查找
 *
 * Minimum Bounding Polygon (Convex Hull; Smallest Enclosing A Set of Points)
 * <b><a href="http://alienryderflex.com/smallest_enclosing_polygon/">©2009 Darel Rex Finley.</a></b>
 *
 *  y
 *  ↑   ·  ·
 *  │  · ·   ·
 *  │ ·  · ·   ·
 *  │  ·  ·
 * —│————————————→ x
 *
 * </pre>
 *
 * @author  ManerFan 2015年4月17日
 */
public class MinimumBoundingPolygon {

    public static LinkedList<Point> findSmallestPolygon(List<Point> ps) {
        if (null == ps || ps.isEmpty()) {
            return null;
        }

        Point corner = findStartPoint(ps);
        if (null == corner) {
            return null;
        }

        double minAngleDif, oldAngle = 2 * Math.PI;
        LinkedList<Point> bound = new LinkedList<>();
        do {
            minAngleDif = 2 * Math.PI;

            bound.add(corner);

            Point nextPoint = corner;
            double nextAngle = oldAngle;
            for (Point p : ps) {
                if (p.founded) { // 已被加入边界链表的点
                    continue;
                }

                if (p.equals(corner)) { // 重合点
                    /*if (!p.equals(bound.getFirst())) {
                        p.founded = true;
                    }*/
                    continue;
                }

                double currAngle = DiscretePointUtil.angleOf(corner, p); /* 当前向量与x轴正方向的夹角 */
                double angleDif = DiscretePointUtil.reviseAngle(oldAngle - currAngle); /* 两条向量之间的夹角（顺时针旋转的夹角） */

                if (angleDif < minAngleDif) {
                    minAngleDif = angleDif;
                    nextPoint = p;
                    nextAngle = currAngle;
                }
            }

            oldAngle = nextAngle;
            corner = nextPoint;
            corner.founded = true;
        } while (!corner.equals(bound.getFirst())); /* 判断边界是否闭合 */

        return bound;
    }

    /** 查找起始点（保证y最大的情况下、尽量使x最小的点） */
    private static Point findStartPoint(List<Point> ps) {
        if (null == ps || ps.isEmpty()) {
            return null;
        }

        Point p = ps.get(0);
        ListIterator<Point> iter = ps.listIterator();

        while (iter.hasNext()) {
            Point point = iter.next();
            if (point.getY() > p.getY() || (point.getY() == p.getY() && point.getX() < p.getX())) { /* 找到最靠上靠左的点 */
                p = point;
            }
        }

        return p;
    }
}
