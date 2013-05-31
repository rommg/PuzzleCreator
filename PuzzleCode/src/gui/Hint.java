package gui;

import javax.swing.JComponent;

/*
 * A hint in a HintPopupMenu
 */
class Hint implements Comparable<Hint>{

	private String predicateString = null;
	private String replacer = null;
	String hintText = null;
	HintPopupMenu parent = null;
	
	public String getHintText() {
		return hintText;
	}

	
	public HintPopupMenu getParentPopup() {
		return parent;
	}

	public Hint(String replacer, String predicateString, JComponent parent) {
		this.replacer = replacer;
		this.predicateString = predicateString;
		this.parent = (HintPopupMenu)parent;
		buildHintText();
	}

	@Override
	/**
	 * comparing predicates because we want the order of hint types to always be the same, not dependent on the entity
	 */
	public int compareTo(Hint o) { 
		return (this.predicateString.compareTo(o.predicateString));
	} 

	/*
	 * implant the entity's name instead of the '?'
	 */
	private void buildHintText() {
		hintText = predicateString.replaceFirst("\\?", replacer);
	}
}
