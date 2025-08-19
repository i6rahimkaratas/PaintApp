import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class DrawingCanvas extends JPanel {

    public enum Tool { PEN, ERASER, LINE, RECTANGLE, OVAL, ROUND_RECT }
    private Tool currentTool = Tool.PEN;

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
                previewShape = new Path(new ArrayList<>(pathPoints), currentColor, currentSize);
            } else {
                previewShape = createShape();
            }
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (currentTool == Tool.PEN || currentTool == Tool.ERASER) {
                if (pathPoints != null && pathPoints.size() > 1) {
                    shapes.add(new Path(pathPoints, currentColor, currentSize));
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
                    return new Line(startPoint, endPoint, currentColor, currentSize);
                case RECTANGLE:
                    return new Rectangle(x, y, width, height, currentColor, currentSize);
                case OVAL:
                    return new Oval(x, y, width, height, currentColor, currentSize);
                case ROUND_RECT:
                    return new RoundRectangle(x, y, width, height, currentColor, currentSize);
                default:
                    return null;
            }
        }
    }

    private class Line implements DrawableShape {
        Point start, end;
        Color color;
        int size;
        public Line(Point start, Point end, Color color, int size) {
            this.start = start; this.end = end; this.color = color; this.size = size;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(new BasicStroke(this.size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(this.start.x, this.start.y, this.end.x, this.end.y);
        }
    }

    private class Path implements DrawableShape {
        java.util.List<Point> points;
        Color color;
        int size;
        public Path(java.util.List<Point> points, Color color, int size) {
            this.points = points; this.color = color; this.size = size;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(new BasicStroke(this.size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }
    
    private class Rectangle implements DrawableShape {
        Rectangle2D.Float rect;
        Color color;
        int size;
        public Rectangle(int x, int y, int width, int height, Color color, int size) {
            rect = new Rectangle2D.Float(x, y, width, height);
            this.color = color; this.size = size;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(new BasicStroke(this.size));
            g2d.draw(rect);
        }
    }

    private class Oval implements DrawableShape {
        Ellipse2D.Float oval;
        Color color;
        int size;
        public Oval(int x, int y, int width, int height, Color color, int size) {
            oval = new Ellipse2D.Float(x, y, width, height);
            this.color = color; this.size = size;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(new BasicStroke(this.size));
            g2d.draw(oval);
        }
    }

    private class RoundRectangle implements DrawableShape {
        java.awt.geom.RoundRectangle2D.Float rect;
        Color color;
        int size;
        public RoundRectangle(int x, int y, int width, int height, Color color, int size) {
            float arcw = width * 0.3f;
            float arch = height * 0.3f;
            rect = new java.awt.geom.RoundRectangle2D.Float(x, y, width, height, arcw, arch);
            this.color = color; this.size = size;
        }
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(this.color);
            g2d.setStroke(new BasicStroke(this.size));
            g2d.draw(rect);
        }
    }
}
