package assignment5;

import java.awt.Point;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.SwingUtilities;
import java.awt.geom.Point2D.Double;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Regions
{
	
	private Vector<Shape> regions;
	private Vector<String> regionIDs;
	
	public Regions()
	{
		regions = new Vector<Shape>();
		regionIDs = new Vector<String>();
	}
	
	public void addRegion(Shape shape, String shapeID){
		regions.add(shape);
		regionIDs.add(shapeID);
	}
	
	public int getRegionCount()
	{
		return regions.size();
	}
	
	public Shape getRegion(int index)
	{
		return regions.get(index);
	}
	
	public void handleMouseClick(Point point)
	{
		String regionID = null;
		for (int i = 0; i < regions.size(); ++i)
		{
			Shape shape = regions.get(i);
			if (shape.contains((double)point.getX(), (double)point.getY()))
			{
				regionID = regionIDs.get(i);
				break;
			}
		}
		final Vector<Shape> pondInRegion = fetchPondInRegion(regionID);
		final Vector<Double> lionInRegion = fetchLionInRegion(regionID);
		Display.getInstance().handleMouseClick();
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
        		Display.getInstance().setFocusOnPonds(pondInRegion);
        		Display.getInstance().setFocusOnLions(lionInRegion);	
            }
        });
	}
	
	public Vector<Shape> fetchPondInRegion(String regionID)
	{
		Vector<Shape> pondInRegion = new Vector<Shape>();
		
		Statement stmt = ConnectionManager.getStatement();
		STRUCT struct = null;
		JGeometry geoObject = null;
		ResultSet result = null;
		
		try {
			String query = "SELECT P.shape " + 
					"FROM TABLE(SDO_JOIN('POND', 'SHAPE', 'REGION', 'SHAPE','mask=INSIDE')) PR, pond P, region R " + 
					"WHERE PR.rowid1 = P.rowid AND PR.rowid2 = R.rowid AND R.region_id = '" + regionID + "'";
			result = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Query execution Failed!");
			e.printStackTrace();
		}
		try {
			while(result.next()) {
				struct = (STRUCT) result.getObject(1);	
				geoObject = JGeometry.load(struct);
				Shape shape = geoObject.createShape();
				pondInRegion.add(shape);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pondInRegion;
	}

	public Vector<Double> fetchLionInRegion(String regionID)
	{
		Vector<Double> lionInRegion = new Vector<Double>();
		
		Statement stmt = ConnectionManager.getStatement();
		STRUCT struct = null;
		JGeometry geomObject = null;
		ResultSet result = null;
		
		try {
			String query = "SELECT L.shape " +
					"FROM TABLE(SDO_JOIN('LION', 'SHAPE', 'REGION', 'SHAPE','mask=INSIDE')) LR, lion L, region R " +
					"WHERE LR.rowid1 = L.rowid AND LR.rowid2 = R.rowid AND R.region_id = '" + regionID + "'";
			result = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Query execution Failed!");
			e.printStackTrace();
		}
		try {
			while(result.next()) {
				struct = (STRUCT) result.getObject(1);	
				geomObject = JGeometry.load(struct);
				double coords[]  = geomObject.getPoint();
				Double point = new Double(coords[0], coords[1]);

				lionInRegion.add(point);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lionInRegion;		
	}
	
	@SuppressWarnings("deprecation")
	public void fetchRegion()
	{
		Statement stmt = ConnectionManager.getStatement();
		STRUCT struct = null;
		JGeometry geoObject = null;
		ResultSet result = null;
		
		try {
			result = stmt.executeQuery("select * from region");
		} catch (SQLException e) {
			System.out.println("Query Failed!");
			e.printStackTrace();
		}
		try {
			while(result.next()) {
				String regionID = result.getString(1);
				/*System.out.println("Region ID: " + regionID);*/
				struct = (STRUCT) result.getObject(3);	
				geoObject = JGeometry.load(struct);
				Shape shape = geoObject.createShape();
				addRegion(shape, regionID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}