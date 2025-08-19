import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class DrawingCanvas extends JPanel {

    public enum Tool { PEN, ERASER, LINE, RECTANGLE, OVAL, ROUND_RECT }
    public enum BrushTip { ROUND, SQUARE }
    public enum BrushStyle { SOLID, DASHED, DOTTED }

    private Tool currentTool = Tool.PEN;
    private BrushTip currentBrushTip = BrushTip.ROUND;
    private BrushStyle currentBrushStyle = BrushStyle.SOLID;

    private Point startPoint = null;
    private Point endPoint = null;

    private Color drawColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private Color currentColor = drawColor;
    private int currentSize = 5;

    private ArrayList<DrawableShape> shapes = new ArrayList<>();
    private DrawableShape previewShape = null;

    public DrawingCanvas() {
        this.setBackground(backgroundColor);
        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (DrawableShape shape : shapes) {
            shape.draw(g2d);
        }
        if (previewShape != null) {
            previewShape.draw(g2d);
        }
    }

    public void setCurrentTool(Tool tool) {
        this.currentTool = tool;
        switch (tool) {
            case PEN:
            case LINE:
            case RECTANGLE:
            case OVAL:
            case ROUND_RECT:
                usePen();
                break;
            case ERASER:
                useEraser();
                break;
        }
    }

    public void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Bir Renk Seçin", drawColor);
        if (newColor != null) {
            drawColor = newColor;
            if (currentTool != Tool.ERASER) {
                usePen();
            }
        }
    }

    public void usePen() {
        currentColor = drawColor;
    }

    public void useEraser() {
        currentColor = backgroundColor;
    }

    public void setBrushSize(int size) {
        this.currentSize = size;
    }

    public void setBrushTip(BrushTip tip) {
        this.currentBrushTip = tip;
    }

    public void setBrushStyle(BrushStyle style) {
        this.currentBrushStyle = style;
    }

    public void clearCanvas() {
        shapes.clear();
        previewShape = null;
        repaint();
    }

    private class MouseHandler extends MouseAdapter {
        private ArrayList<Point> pathPoints;

        @Override
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
            endPoint = startPoint;
            if (currentTool == Tool.PEN || currentTool == Tool.ERASER) {
                pathPoints = new ArrayList<>();
                pathPoints.add(startPoint);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            endPoint = e.getPoint();
            if (currentTool == Tool.PEN || currentTool == Tool.ERASER) {
                pathPoints.add(endPoint);
                previewShape = new Path(new ArrayList<>(pathPoints), currentColor, currentSize, currentBrushTip, currentBrushStyle);
            } else {
                previewShape = createShape();
            }
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (currentTool == Tool.PEN || currentTool == Tool.ERASER) {
                if (pathPoints != null && pathPoints.size() > 1) {
                    shapes.add(new Path(pathPoints, currentColor, currentSize, currentBrushTip, currentBrushStyle));
                }
            } else {
                DrawableShape finalShape = createShape();
                if (finalShape != null) {
                    shapes.add(finalShape);
                }
            }
            previewShape = null;
            pathPoints = null;
            startPoint = null;
            endPoint = null;
            repaint();
        }

        private DrawableShape createShape() {
            if (startPoint == null || endPoint == null) return null;
            int x = Math.min(startPoint.x, endPoint.x);
            int y = Math.min(startPoint.y, endPoint.y);
            int width = Math.abs(startPoint.x - endPoint.x);
            int height = Math.abs(startPoint.y - endPoint.y);
            switch (currentTool) {
                case LINE:
                    return new Line(startPoint, endPoint, currentColor, currentSize, currentBrushTip, currentBrushStyle);
                case RECTANGLE:
                    return new Rectangle(x, y, width, height, currentColor, currentSize, currentBrushTip, currentBrushStyle);
                case OVAL:
                    return new Oval(x, y, width, height, currentColor, currentSize, currentBrushTip, currentBrushStyle);
                case ROUND_RECT:
                    return new RoundRectangle(x, y, width, height, currentColor, currentSize, currentBrushTip, currentBrushStyle);
                default:
                    return null;
            }
        }
    }

    // --- Ortak Stroke oluşturma mantığı için abstract class ---
    private abstract class BaseShape implements DrawableShape {
        Color color;
        int size;
        BrushTip tip;
        BrushStyle style;

        protected Stroke createStroke() {
            int cap = (tip == BrushTip.SQUARE) ? BasicStroke.CAP_SQUARE : BasicStroke.CAP_ROUND;
            int join = (tip == BrushTip.SQUARE) ? BasicStroke.JOIN_MITER : BasicStroke.JOIN_ROUND;

            if (style == BrushStyle.SOLID) {
                return new BasicStroke(this.size, cap, join);
            } else {
                float[] dashPattern;
                if (style == BrushStyle.DASHED) {
                    dashPattern = new float[]{10, 10};
                } else { // DOTTED
                    dashPattern = new float[]{Math.max(2, this.size), this.size * 2f};
                    cap = BasicStroke.CAP_ROUND;
                }
                return new BasicStroke(this.size, cap, join, 10.0f, dashPattern, 0.0f);
            }
        }
    }

    private class Line extends BaseShape {
        Point start, end;
        public Line(Point start, Point end, Color color, int size, BrushTip tip, BrushStyle style) {
            this.start = start; this.end = end; this.color = color; this.size = size; this.tip = tip; this.style = style;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(createStroke());
            g2d.drawLine(this.start.x, this.start.y, this.end.x, this.end.y);
        }
    }

    private class Path extends BaseShape {
        java.util.List<Point> points;
        public Path(java.util.List<Point> points, Color color, int size, BrushTip tip, BrushStyle style) {
            this.points = points; this.color = color; this.size = size; this.tip = tip; this.style = style;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(createStroke());
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    private class Rectangle extends BaseShape {
        Rectangle2D.Float rect;
        public Rectangle(int x, int y, int width, int height, Color color, int size, BrushTip tip, BrushStyle style) {
            rect = new Rectangle2D.Float(x, y, width, height);
            this.color = color; this.size = size; this.tip = tip; this.style = style;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(createStroke());
            g2d.draw(rect);
        }
    }

    private class Oval extends BaseShape {
        Ellipse2D.Float oval;
        public Oval(int x, int y, int width, int height, Color color, int size, BrushTip tip, BrushStyle style) {
            oval = new Ellipse2D.Float(x, y, width, height);
            this.color = color; this.size = size; this.tip = tip; this.style = style;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(createStroke());
            g2d.draw(oval);
        }
    }

    private class RoundRectangle extends BaseShape {
        RoundRectangle2D.Float rect;
        public RoundRectangle(int x, int y, int width, int height, Color color, int size, BrushTip tip, BrushStyle style) {
            float arcw = width * 0.3f;
            float arch = height * 0.3f;
            rect = new RoundRectangle2D.Float(x, y, width, height, arcw, arch);
            this.color = color; this.size = size; this.tip = tip; this.style = style;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(createStroke());
            g2d.draw(rect);
        }
    }
}