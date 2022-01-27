package it.davideacanfora.sudoku;

import java.util.ArrayList;

public class MessageListenerImpl implements MessageListener {
	private ArrayList<String> messages;
	
	public MessageListenerImpl(ArrayList<String> messages) {
		this.messages = messages;
	}
	
	@Override
	public Object parseMessage(Object obj) {
		this.messages.add(obj.toString());
		return true;
	}
	
}