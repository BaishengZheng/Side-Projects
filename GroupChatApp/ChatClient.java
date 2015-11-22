import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame {
	
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	TextField tfTxt = new TextField();
	TextArea taContent = new TextArea();
	private boolean bConnect = false;

	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}
	
	/*initialize the ChatClient frame and run a new thread to communicate with the ChatServer*/
	public void launchFrame() {
		setLocation(400, 300);
		this.setSize(300, 300);
		add(tfTxt, BorderLayout.SOUTH);
		add(taContent, BorderLayout.NORTH);
		pack();
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}

		});
		tfTxt.addActionListener(new TFListener());
		setVisible(true);
		connect();
		new Thread(new RecvThread()).start();
	}
	
	/*connect() will initialize the connection with ChatServer */
	public void connect() {
		try {
			s = new Socket("127.0.0.1", 8888);
			bConnect = true;
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
System.out.println("connected!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/*disconnect() will disconnect with the ChatServer*/
	public void disconnect () {
		try {
			if (dos != null) dos.close();
			if (dis != null) dis.close();
			if (s!= null) s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*TFListener will react when the ChatClient want to send its message*/
	private class TFListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String str = tfTxt.getText().trim();
			tfTxt.setText("");
			
			try {
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
		
	}

	/*RecvThread is a thread waiting for the messages from ChatServer*/
	private class RecvThread implements Runnable {

		@Override
		public void run() {
			try {
				while (bConnect) {
					String str = dis.readUTF();
					taContent.setText(taContent.getText() + str + '\n');
				}
			} catch(EOFException e) {
				System.out.println("sever is closed");
			} catch (SocketException e) {
				System.out.println("Client is closed");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
}
