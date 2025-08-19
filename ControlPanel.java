import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ControlPanel extends JPanel {

    private DrawingCanvas canvas;

    public ControlPanel(DrawingCanvas canvas) {
        this.canvas = canvas;
        this.setLayout(new FlowLayout(FlowLayout.LEFT)); 

        JButton colorButton = new JButton("Renk Seç");
        colorButton.addActionListener(e -> this.canvas.chooseColor());

        JButton clearButton = new JButton("Temizle");
        clearButton.addActionListener(e -> this.canvas.clearCanvas());

        
        JButton penButton = new JButton("Kalem");
        penButton.addActionListener(e -> this.canvas.usePen());
        
        JButton eraserButton = new JButton("Silgi");
        eraserButton.addActionListener(e -> this.canvas.useEraser());

       
        JLabel sizeLabel = new JLabel("Boyut: 5");
        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 5); 
        
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                
                int newSize = sizeSlider.getValue(); 
                sizeLabel.setText("Boyut: " + newSize); 
                canvas.setBrushSize(newSize); 
            }
        });
        
        
        this.add(colorButton);
        this.add(penButton);
        this.add(eraserButton);
        this.add(clearButton);
        this.add(sizeLabel);
        this.add(sizeSlider);
    }
}