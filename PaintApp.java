import javax.swing.*;
import java.awt.*;

public class PaintApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Java Paint");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        DrawingCanvas canvas = new DrawingCanvas();
        
        // BU SATIR ÇOK ÖNEMLİ: Kendi ControlPanel sınıfımızı kullanıyoruz.
        ControlPanel controlPanel = new ControlPanel(canvas);

        frame.add(canvas, BorderLayout.CENTER);
        
        // Paneli pencerenin ÜSTÜNE (NORTH) ekliyoruz.
        frame.add(controlPanel, BorderLayout.NORTH);

        // ÖNEMLİ: Bileşenleri ekledikten sonra pencerenin boyutunu ayarlamak daha iyidir.
        // pack() metodu, içindeki bileşenlere göre pencereyi en uygun boyuta getirir.
        frame.pack(); 
        
        // Veya minimum bir boyut belirleyebilirsiniz.
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setSize(800, 600); // Yine de başlangıç boyutu belirleyelim.

        frame.setVisible(true);
    }
}