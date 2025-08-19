import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ControlPanel extends JPanel {

    private DrawingCanvas canvas;

    public ControlPanel(DrawingCanvas canvas) {
        this.canvas = canvas;
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        ButtonGroup toolGroup = new ButtonGroup();
        Dimension buttonSize = new Dimension(40, 40);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new GridLayout(2, 1, 5, 5));
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Araçlar"));
        
        JToggleButton penButton = createToolButton(DrawingCanvas.Tool.PEN, toolGroup, buttonSize);
        JToggleButton eraserButton = createToolButton(DrawingCanvas.Tool.ERASER, toolGroup, buttonSize);
        toolsPanel.add(penButton);
        toolsPanel.add(eraserButton);
        
        JPanel shapesPanel = new JPanel();
        shapesPanel.setLayout(new GridLayout(2, 2, 5, 5));
        shapesPanel.setBorder(BorderFactory.createTitledBorder("Şekiller"));
        
        JToggleButton lineButton = createToolButton(DrawingCanvas.Tool.LINE, toolGroup, buttonSize);
        JToggleButton rectButton = createToolButton(DrawingCanvas.Tool.RECTANGLE, toolGroup, buttonSize);
        JToggleButton ovalButton = createToolButton(DrawingCanvas.Tool.OVAL, toolGroup, buttonSize);
        JToggleButton roundRectButton = createToolButton(DrawingCanvas.Tool.ROUND_RECT, toolGroup, buttonSize);
        shapesPanel.add(lineButton);
        shapesPanel.add(rectButton);
        shapesPanel.add(ovalButton);
        shapesPanel.add(roundRectButton);

        penButton.setSelected(true);

        JPanel otherControls = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton colorButton = new JButton("Renk Seç");
        JButton clearButton = new JButton("Temizle");
        otherControls.add(colorButton);
        otherControls.add(clearButton);
        colorButton.addActionListener(e -> this.canvas.chooseColor());
        clearButton.addActionListener(e -> this.canvas.clearCanvas());

        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.Y_AXIS));
        JLabel sizeLabel = new JLabel("Boyut: 5");
        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 5);
        sizeSlider.setPreferredSize(new Dimension(150, 20));
        sizePanel.add(sizeLabel);
        sizePanel.add(sizeSlider);
        sizeSlider.addChangeListener(e -> {
            int newSize = sizeSlider.getValue();
            sizeLabel.setText("Boyut: " + newSize);
            canvas.setBrushSize(newSize);
        });

        this.add(toolsPanel);
        this.add(shapesPanel);
        this.add(otherControls);
        this.add(sizePanel);
    }

    private JToggleButton createToolButton(DrawingCanvas.Tool tool, ButtonGroup group, Dimension size) {
        ImageIcon icon = createShapeIcon(tool, size.width - 10, size.height - 10);
        JToggleButton button = new JToggleButton(icon);
        button.setToolTipText(tool.toString());
        button.setPreferredSize(size);
        button.addActionListener(e -> canvas.setCurrentTool(tool));
        group.add(button);
        return button;
    }

    private ImageIcon createShapeIcon(DrawingCanvas.Tool tool, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        int pad = 4;

        switch (tool) {
            case PEN:
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(pad, height - pad, width / 2, pad);
                g2d.drawLine(width / 2, pad, width - pad, height - pad);
                g2d.fillRect(width / 2 - 2, pad - 2, 4, 4);
                break;
            case ERASER:
                g2d.setColor(new Color(230, 230, 230));
                g2d.fillRect(pad, pad, width - (2 * pad), height - (2 * pad));
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRect(pad, pad, width - (2 * pad), height - (2 * pad));
                break;
            case LINE:
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(pad, height - pad, width - pad, pad);
                break;
            case RECTANGLE:
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(pad, pad, width - (2 * pad), height - (2 * pad));
                break;
            case OVAL:
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(pad, pad, width - (2 * pad), height - (2 * pad));
                break;
            case ROUND_RECT:
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(pad, pad, width - (2 * pad), height - (2 * pad), 8, 8);
                break;
        }

        g2d.dispose();
        return new ImageIcon(image);
    }
}
