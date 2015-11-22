import java.io.*;
import java.net.*;
import java.util.*;

/*
This class ChatServer should run first, then it will wait for the requests from ChatClient.
When the ChatServer receives the messages from one ChatClient, it will broadcast to each 
ChatClient immediately.
 */
public class ChatServer {
	//use a List clients to record all the running threads, so if one thread dies,
	//we also need to update it.
	List<Client> clients = new ArrayList<Client> ();

	public static void main(String[] args) {
		new ChatServer().start();
	}

	public void start() {
		boolean started = false;
		ServerSocket ss = null;
		
		/*initialize the ServerSocket*/
		try {
			ss = new ServerSocket(8888);
			started = true;
		} catch (BindException e) {
			System.out.println("The port is being used!");
			System.out.println("Please close the related program and relaunch the server!");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		/*ChatServer waits for requests from ChatClients*/
		try {
			while (started) {
				Socket s = ss.accept();// wait for connect
				Client c = new Client(s);
				System.out.println("a client connected");
				new Thread(c).start();
				clients.add(c);
			}
		} catch (IOException e) {			
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*Client is an encapsulated class used to initialize a new thread to communicate with 
	  a ChatClient*/
	class Client implements Runnable {
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bConnected = false;
		
		public Client(Socket s) {
			this.s = s;
			try {			
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void sendToClient(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while (bConnected) {	
					String str = dis.readUTF();
//System.out.println(str);
					for(int i = 0; i<clients.size(); i++) {
						Client c = clients.get(i);
						c.sendToClient(str);
					}
				}
			} catch (EOFException e) {
				System.out.println("Client disconnect!");
				clients.remove(this);
			} catch (IOException e) {			
				e.printStackTrace();
			} finally {
				try {
					if (s != null) s.close();
					if (dis != null) dis.close();
					if (dos != null) dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}			
		}		
	}
}
