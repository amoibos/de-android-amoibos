package de.interpreter.brainfuck;

import java.util.HashMap;
import java.util.Stack;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Daniel Ölschlegel
 * @date 2012/03
 * @license 2-BSDL
 * @version 0.2
 */

public class BfInterpreter extends Thread {
	private byte[] memory = new byte[256 * 256];
	private char[] code = new char[256 * 256 + 1];
	private int memoryPointer;
	private int codeSize = 0;
	private Activity mainWindow;
	private HashMap<Integer, Integer> jumpTable;
	static char inputChar = '\0';
	private int ip = 0;
	private Handler myHandler;
	private StringBuilder output;
	public boolean blocking = false;
	
	public String getOutput() {
		
		return output.toString();
	}
	
	public void setHandler(Handler handler) {
		
		this.myHandler = handler;
	}
	
	public BfInterpreter(String code, int ip, BrainfuckActivity window) {
		this.mainWindow = window;
		this.ip = ip;
		output = new StringBuilder();
		jumpTable = new HashMap<Integer, Integer>();
		
		Stack<Integer> stack = new Stack<Integer>();
		int top;
		for (int i = 0; i < code.length(); ++i) {
			// brainfuck code only
			if (!"[]+-<>.,".contains("" + code.charAt(i)))
				continue;
			this.code[codeSize] = code.charAt(i);
			if (this.code[codeSize] == '[')
				stack.add(codeSize);
			else if (this.code[codeSize] == ']') {
				if (stack.isEmpty()) {
					this.code[0] = '\0';
					return;
				}
				top = stack.pop();
				jumpTable.put(top, codeSize);
				jumpTable.put(codeSize, top);
			}
			++codeSize;
		}
		this.code[codeSize] = '\0';
		if (stack.isEmpty())
			reset();
		else
			this.code[0] = '\0';
	}
	
	private void reset() {
		memoryPointer = 0;
		for (int i = 0; i < memory.length; ++i)
			memory[i] = '\0';
		output = new StringBuilder();
	}
	
	//TODO: compatibility
	public void run() {
		while (true) {
			switch (code[ip]) {
			case '+':
				if (memory[memoryPointer] < 255)
					++memory[memoryPointer];
				break;
			case '-':
				if (memory[memoryPointer] > 0)
					--memory[memoryPointer];
				break;
			case '>':
				if (memoryPointer < 256 * 256 - 1)
					++memoryPointer;
				break;
			case '<':
				if (memoryPointer > 0)
					--memoryPointer;
				break;
			case '.':
				output.append((char) memory[memoryPointer]);
				break;
			case ',':
				showDialog();
				memory[memoryPointer] = (byte) inputChar;
				break;
			case '[': 
				if (memory[memoryPointer] == 0) 
					ip = jumpTable.get(ip);
					break;
			case ']':
				if (memory[memoryPointer] != 0) {
					ip = jumpTable.get(ip);
					continue;
				}
				break;
			case '\0': // Exit
				Message msg = myHandler.obtainMessage();
				msg.obj = getOutput();
				myHandler.sendMessage(msg);
				return;
			default: // ignore rest
				break;
			}
			++ip;
		}
	}
	
	public void showDialog() {
		Intent intent = new Intent(mainWindow, InputDialog.class);
		mainWindow.startActivityForResult(intent, 0);
		blocking = true;
		while (blocking);
	}
			
}
