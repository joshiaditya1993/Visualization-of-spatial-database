package assignment5;

public class Main {
	public static void main(String[] args) 
	{
		ConnectionManager dbManager = new ConnectionManager();
		dbManager.Connect();
		
		Regions regions = new Regions();
		regions.fetchRegion();
		
		Ponds ponds = new Ponds();
		ponds.fetchPond();

		Lions lions = new Lions();
		lions.fetchLion();
		
		Display ui = Display.getInstance();
		ui.createUI(regions, ponds, lions);
	}

}
