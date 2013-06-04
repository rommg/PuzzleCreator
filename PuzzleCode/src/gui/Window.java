package gui;

/*
 * enum for window names
 */
enum Window {
	Welcome ("Welcome"),
	PrepareGame ("PrepareGame"),
	Wait("WaitView"),
	HallOfFame("HallOfFame"),
	Crossword("CrosswordView"),
	AddDefinition("AddDefinition"),
	AddHint("AddHint"),
	MassiveImport("MassiveImport"),
	Help("Help"),
	About("About");

	private String name;       

	private Window(String s) {
		name = s;
	}

	@Override
	public String toString() {
		return name;
	}
	
	
}
