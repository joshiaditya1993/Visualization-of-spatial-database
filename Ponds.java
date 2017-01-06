package assignment5;

import java.awt.Shape;
import java.util.Vector;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Ponds {

	private Vector<Shape> ponds;
	public Ponds()
	{
		ponds = new Vector<Shape>();
	}
	
	public void addPond(Shape shape){
		ponds.add(shape);
	}
	
	public int getPondCount()
	{
		return ponds.size();
	}
		
	public Shape getPond(int index)
	{
		return ponds.get(index);
	}
	@SuppressWarnings("deprecation")
	public void fetchPond()
	{
		Statement stmt = ConnectionManager.getStatement();
		STRUCT struct = null;
		JGeometry geoObject = null;
		ResultSet result = null;
		
		try {
			result = stmt.executeQuery("select * from pond");
		} catch (SQLException e) {
			System.out.println("Query Failed!");
			e.printStackTrace();
		}
		try {
			while(result.next()) {
				/*System.out.println("Pond ID: " + result.getString(1));*/
				struct = (STRUCT) result.getObject(2);	
				geoObject = JGeometry.load(struct);
				Shape shape = geoObject.createShape();
				addPond(shape);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}
