package web.server.util;

import web.server.bean.Dot;

public class AreaChecker {
    public Dot checkArea(Dot dot)  {
        boolean isInArea = isInArea(dot.getX(), dot.getY(), dot.getR());
        dot.setInArea(isInArea);
        return dot;
    }

    private boolean isInArea(Double x, Double y, Double r) {

        if (x >= 0 && y >= 0) {
            return isInSquare(x, y, r);
        } else if ((x >= 0 && y <= 0)) {
            return isInCircle(x, y, r);
        } else if (x <= 0 && y <= 0) {
            return isInTriangle(x, y, r);
        }

        return false;
    }

    private boolean isInCircle(double x, double y, double r) {
        return Math.sqrt(x * x + y * y) <= r / 2;
    }

    private boolean isInSquare(double x, double y, double r) {
        return x < r && y < r;
    }

    private boolean isInTriangle(double x, double y, double r) {
        return y > -2 * x - r;
    }
}
