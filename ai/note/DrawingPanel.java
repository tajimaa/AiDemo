package ai.note;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class DrawingPanel extends JPanel {
    private static final int WIDTH = 28;
    private static final int HEIGHT = 28;
    private int[][] pixels = new int[WIDTH][HEIGHT];
    private String target = null;
    private int lastX = -1;
    private int lastY = -1;
    private JTextArea targetTextArea;

    public DrawingPanel() {
        setPreferredSize(new Dimension(WIDTH * 20, HEIGHT * 20));

        addMouseListener(new DrawingMouseListener());
        addMouseMotionListener(new DrawingMouseListener());

        JButton resetButton = new JButton("クリア");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearPixels();
            }
        });

        JButton sendButton = new JButton("送信");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                target = targetTextArea.getText(); 
                
                
                DataSender.sendData(pixels, target);
                clearPixels();
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        buttonPanel.add(sendButton);

        targetTextArea = new JTextArea(1, 20);
        targetTextArea.setLineWrap(true);
        targetTextArea.setWrapStyleWord(true);

        JPanel textAreaPanel = new JPanel();
        textAreaPanel.add(new JScrollPane(targetTextArea)); 

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
        add(textAreaPanel, BorderLayout.NORTH);
    }

    private void clearPixels() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                pixels[x][y] = 0; 
            }
        }
        repaint();
    }

    private class DrawingMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX() / 20;
            int y = e.getY() / 20;
            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                pixels[x][y] = 1; 
                lastX = x;
                lastY = y;
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            lastX = -1;
            lastY = -1;
            target = targetTextArea.getText(); 
            
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX() / 20;
            int y = e.getY() / 20;
            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                if (lastX != -1 && lastY != -1) {
                    drawLine(lastX, lastY, x, y);
                }
                lastX = x;
                lastY = y;
                repaint();
            }
        }
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x1 * 20 + 10, y1 * 20 + 10, x2 * 20 + 10, y2 * 20 + 10);
        pixels[x2][y2] = 1;
        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (pixels[x][y] == 1) {
                    g.setColor(Color.BLACK);
                    g.fillRect(x * 20, y * 20, 20, 20);
                }
            }
        }

        g.setColor(Color.GRAY);
        for (int x = 0; x <= WIDTH; x++) {
            g.drawLine(x * 20, 0, x * 20, HEIGHT * 20);
        }
        for (int y = 0; y <= HEIGHT; y++) {
            g.drawLine(0, y * 20, WIDTH * 20, y * 20);
        }
    }
}
