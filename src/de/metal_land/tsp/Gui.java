package de.metal_land.tsp;

import javax.swing.*;
import java.awt.*;

/**
 * @author nieh
 */
public class Gui implements Runnable{

    private TSP tsp;
    private Route routeToDraw;
    private JFrame window;
    private int repaints = 0;
    private Label repaintCounter;


    public Gui(TSP tsp){
        this.tsp = tsp;
        this.routeToDraw = tsp.getBestRoute();
    }

    @Override
    public void run() {
        createWindow();
    }

    /**
     * Creates the Window.
     */
    private void createWindow(){
        window = new JFrame("TSP");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        TspPanel panel = new TspPanel();
        window.add(panel, BorderLayout.CENTER);

        repaintCounter = new Label("Repaints: ");
        window.add(repaintCounter, BorderLayout.PAGE_END);

        //window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        window.pack();
        window.setVisible(true);

        tsp.setListener(new DataChangedEventListener() {
            @Override
            public void changed(Route route) {
                routeToDraw = route;
                window.repaint();
            }
        });
    }

    /**
     * A special Panel to visualize the solution attempt for the TSP.
     */
    private class TspPanel extends JPanel{

        public TspPanel(){
           setBorder(BorderFactory.createLineBorder(Color.black));
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            int maxX = 1;
            int maxY = 1;
            for (Node node : tsp.getNodes()) {
                maxX = (node.getX() > maxX)? node.getX() : maxX;
                maxY = (node.getY() > maxY)? node.getY() : maxY;
            }

            double factorX = maxX / (getSize().getWidth()-22);
            double factorY = maxY / (getSize().getHeight()-22);

            for (Node node : tsp.getNodes()) {
                g.fillRoundRect((int) Math.round(node.getX() / factorX) + 10,
                        (int) Math.round(node.getY() / factorY) + 10, 4, 4, 4, 4);
            }

            Node lastNode = null;
            for (Node node : routeToDraw.getRoute()) {
                if(lastNode == null){
                    lastNode = node;
                    continue;
                }

                g.drawLine((int) Math.round(lastNode.getX() / factorX) + 12,
                        (int) Math.round(lastNode.getY() / factorY) + 12,
                        (int) Math.round(node.getX() / factorX) + 12,
                        (int) Math.round(node.getY() / factorY) + 12);

                lastNode = node;
            }

            if(lastNode != null){
                Node firstNode = routeToDraw.getRoute().get(0);
                g.drawLine((int) Math.round(lastNode.getX() / factorX) + 12,
                        (int) Math.round(lastNode.getY() / factorY) + 12,
                        (int) Math.round(firstNode.getX() / factorX) + 12,
                        (int) Math.round(firstNode.getY() / factorY) + 12);
            }
            repaintCounter.setText(String.format("Repaints: %d", repaints));
            repaints++;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800,500);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(800,500);
        }
    }

    /**
     * Should be called if the Route has changed for redrawing.
     */
    public interface DataChangedEventListener {
        public void changed(Route route);
    }
}
