package utils;

import java.util.List;


public interface GuiDBConnector {
	

	
	/**
	 * Pull a question and an answer from the DB
	 * @return
	 */
	public String[] getTriviaQuestion();
	
	
	public List<String> getTopics();
	
	/*
	 * Add functionality for Add definition 
	 * 
	 * 
	 */
	
	
}
