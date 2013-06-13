package control;

import java.util.Date;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class LogController
{
	private SuperController superC;
	private JTextArea log;
	
	public LogController(SuperController superC)
	{
		this.superC = superC;
		log = new JTextArea();
		log.setCaretPosition(0);
		log.setEditable(false);
		log.setColumns(15);
		log.setRows(3);
	}
	
	public JTextArea getLog()
	{
		return log;
	}
	
	public void appendTop(String message)
	{
		try {
            log.getDocument().insertString(0, new Date() + ": " + message + " \n", null);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
	}
	
	public void append(String message)
	{
		log.append(new Date() + ": " + message + "\n");
	}
}
