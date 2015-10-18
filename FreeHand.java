import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

public class FreeHand extends JPanel {
	BufferedImage image;
	Color color;
	Stroke stroke;

	public FreeHand() {
		color = Color.blue;
		stroke = new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null)
			initImage();
		g.drawImage(image, 0, 0, this);
	}

	public void draw(Point start, Point end) {
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(color);
		g2.setStroke(stroke);
		g2.draw(new Line2D.Double(start, end));
		g2.dispose();
		repaint();
	}

	private void clearImage() {
		Graphics g = image.getGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
		repaint();
	}

	private void initImage() {
		int w = getWidth();
		int h = getHeight();
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		g2.setPaint(getBackground());
		g2.fillRect(0, 0, w, h);
		g2.dispose();
	}

	private JPanel getColorPanel() {
		Color[] colors = { Color.red, Color.green.darker(), Color.blue, Color.orange };
		ActionListener l = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				color = button.getBackground();
			}
		};
		JPanel panel = new JPanel(new GridLayout(1, 0));
		for (int j = 0; j < colors.length; j++) {
			JButton button = new JButton("  ");
			button.setFocusPainted(false);
			button.setBackground(colors[j]);
			button.addActionListener(l);
			panel.add(button);
		}
		return panel;
	}

	private JPanel getControlPanel() {
		JButton clear = new JButton("clear");
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearImage();
			}
		});
		JButton save = new JButton("save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File outputfile = new File("saved.jpg");
				try {
					ImageIO.write(image, "jpg", outputfile);
					Recognize.main(null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.println("save");
			}
		});
		final JSlider slider = new JSlider(JSlider.HORIZONTAL, 5, 10, 6);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float value = ((Integer) slider.getValue()).floatValue();
				stroke = new BasicStroke(value, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
			}
		});
		JPanel panel = new JPanel();
		panel.add(new JLabel("stroke"));
		panel.add(slider);
		panel.add(clear);
		panel.add(save);
		return panel;
	}

	public static void main(String[] args) {
		FreeHand freeHand = new FreeHand();
		DrawingListener listener = new DrawingListener(freeHand);
		freeHand.addMouseListener(listener);
		freeHand.addMouseMotionListener(listener);
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(freeHand.getColorPanel(), "North");
		f.getContentPane().add(freeHand);
		f.getContentPane().add(freeHand.getControlPanel(), "South");
		f.setSize(500, 500);
		f.setLocation(200, 200);
		f.setVisible(true);
	}
}

class DrawingListener extends MouseInputAdapter {
	FreeHand freeHand;
	Point start;

	public DrawingListener(FreeHand fh) {
		this.freeHand = fh;
	}

	public void mousePressed(MouseEvent e) {
		start = e.getPoint();
	}

	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		freeHand.draw(start, p);
		start = p;
	}
}