import javax.swing.*;
import java.awt.*;

public class PaintApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Java Paint");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        DrawingCanvas canvas = new DrawingCanvas();
        
        
        ControlPanel controlPanel = new ControlPanel(canvas);

        frame.add(canvas, BorderLayout.CENTER);
        
       
        frame.add(controlPanel, BorderLayout.NORTH);

        
        frame.pack(); 
        
        
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setSize(800, 600); 

        frame.setVisible(true);
    }
}