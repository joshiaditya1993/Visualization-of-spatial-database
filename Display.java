package assignment5;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Double;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
class MainWindow extends JFrame
{
	private JPanel mainPanel;
	private JCheckBox checkbox;
	
	public JPanel getPanel()
	{
		return mainPanel;
	}
	
	public JCheckBox getCheckBox()
	{
		return checkbox;
	}
	
	public MainWindow() {
        super("Panthera");
        setSize(700, 700);
        
        Container contentPane = getContentPane();
        contentPane.setBackground(Color.WHITE);
        
        mainPanel = new MainPanel();
        mainPanel.setLayout(new BorderLayout());
        contentPane.add(mainPanel);
        
        checkbox = new JCheckBox("Show lions and ponds in region");
        checkbox.setBackground(Color.RED);
        contentPane.add(checkbox, BorderLayout.SOUTH);
        checkbox.setHorizontalAlignment(SwingConstants.CENTER);
        checkbox.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        
        addEventListeners();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
	}
	
	public void addEventListeners()
	{
		checkbox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!checkbox.isSelected())
				{
					mainPanel.repaint();
				}
			}
		});
	}
}


class MainPanel extends JPanel {
	private Regions regions;
	private Ponds ponds;
	private Lions lions;
	
	
	public MainPanel() {
    	regions = null;
    	ponds = null;
    	lions = null;
        setSize(500, 500);
        
        setBackground(Color.WHITE);
        
        addPanelEventListener();
	}

	public void initShapes(Regions regions, Ponds ponds, Lions lions)
	{
		this.regions = regions;
		this.ponds = ponds;
		this.lions = lions;
	}
	
	public void drawRegions(Graphics g)
	{
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(int i = 0; i < regions.getRegionCount(); ++i)
        {
        	Shape region = regions.getRegion(i);
        	g2d.draw(region);
        }
	}

	public void drawPonds(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(int i = 0; i < ponds.getPondCount(); ++i)
		{
			g2d.setColor(Color.BLACK);
			Stroke oldStroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(0.5f));
			Shape pond = ponds.getPond(i);
			Rectangle rBounds = pond.getBounds();
			g2d.drawOval((int)rBounds.getX(), (int)rBounds.getY(), (int)rBounds.getWidth(), (int)rBounds.getHeight());
			g2d.setStroke(oldStroke);
			g2d.setColor(Color.BLUE);
			g2d.fill(pond);
		}
	}
	
	public void drawLions(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(int i = 0; i < lions.getLionCount(); ++i)
		{
			g2d.setColor(Color.GREEN);
			Double location = lions.getLion(i);
			g2d.fillOval((int)location.x, (int)location.y, 5, 5);
		}		
	}
	
	public void addPanelEventListener()
	{
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent evt)
			{
				if (Display.getInstance().isCheckboxSelected())
				{
					regions.handleMouseClick(evt.getPoint());
				}	
			}
		});
	}
	
	@Override
	public void paint(Graphics g) {
        super.paint(g);
        drawRegions(g);
        drawPonds(g);
        drawLions(g);
    }
	
	public void selectPond(Vector<Shape> ponds)
	{
		Graphics g = getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for(int i = 0; i < ponds.size(); ++i)
		{
			Shape pond = ponds.get(i);
			g2d.setColor(Color.RED);
			g2d.fill(pond);
		}		
	}

	public void selectLion(Vector<Double> lions)
	{
		Graphics g = getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for(int i = 0; i < lions.size(); ++i)
		{
			g2d.setColor(Color.RED);
			Double location = lions.get(i);
			g2d.fillOval((int)location.x, (int)location.y, 5, 5);
		}	
	}
}

public class Display {
	private static Display instance = null;
	protected Display() {}
	
	public static Display getInstance()
	{
		if (instance == null)
			instance = new Display();
		
		return instance;
	}
	
	private MainWindow window = null;
	
	public void handleMouseClick()
	{
		((MainPanel) window.getPanel()).repaint();
	}
	
	public void setFocusOnPonds(Vector<Shape> ponds)
	{
		((MainPanel) window.getPanel()).selectPond(ponds);
	}

	public void setFocusOnLions(Vector<Double> lions)
	{
		((MainPanel) window.getPanel()).selectLion(lions);
	}
	
	public boolean isCheckboxSelected()
	{
		return window.getCheckBox().isSelected();
	}
	
	public void createUI(Regions regions, Ponds ponds, Lions lions)
	{
		window = new MainWindow();
		((MainPanel) window.getPanel()).initShapes(regions, ponds, lions);
		
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	window.setVisible(true);
            }
        });
	}
}
