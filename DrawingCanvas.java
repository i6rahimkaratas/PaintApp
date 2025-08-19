import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DrawingCanvas extends JPanel {
    private Point startPoint = null;
    private Point endPoint = null;

    
    private Color drawColor = Color.BLACK;      
    private Color backgroundColor = Color.WHITE;  
    private Color currentColor = drawColor;    
    private int currentSize = 5;               

    private ArrayList<Line> lines = new ArrayList<>();

    public DrawingCanvas() {
        this.setBackground(backgroundColor); 

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                
                lines.add(new Line(startPoint, endPoint, currentColor, currentSize));
                startPoint = endPoint;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Line line : lines) {
            g2d.setColor(line.color);
            
            g2d.setStroke(new BasicStroke(line.size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(line.start.x, line.start.y, line.end.x, line.end.y);
        }
    }

    
    public void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Bir Renk Seçin", drawColor);
        if (newColor != null) {
            drawColor = newColor;
            
            if (currentColor != backgroundColor) {
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
        lines.clear();
        repaint();
    }
    
    
    private class Line {
        Point start;
        Point end;
        Color color;
        int size; 

        public Line(Point start, Point end, Color color, int size) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.size = size;
        }
    }
}