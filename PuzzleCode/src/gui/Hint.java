package gui;

import javax.swing.JComponent;

class Hint implements Comparable<Hint>{

	private String predicateString = null;
	private String replacer = null;
	String hintText = null;
	HintPopup parent = null;
	
	public String getHintText() {
		return hintText;
	}

	
	public HintPopup getParentPopup() {
		return parent;
	}

	public Hint(String replacer, String predicateString, JComponent parent) {
		this.replacer = replacer;
		this.predicateString = predicateString;
		this.parent = (HintPopup)parent;
		buildHintText();
	}

	@Override
	/**
	 * comparing predicates because want the order of hints to always be the same
	 */
	public int compareTo(Hint o) { 
		return (this.predicateString.compareTo(o.predicateString));
	} 

	private void buildHintText() {
		hintText = predicateString.replaceFirst("\\?", replacer);
	}
}
