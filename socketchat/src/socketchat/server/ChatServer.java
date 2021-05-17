package socketchat.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
	private CopyOnWriteArrayList<ClientInfo> clients;
	private ServerSocket serverSock;
	private final int port = 5001;

//	private String readTextFromSocket(Socket sock) throws IOException {
//		InputStreamReader is = new InputStreamReader(sock.getInputStream());
//		char[] b = new char[1024];
//		is.read(b);
//		return new String(b);
//	}	

	public void setClient(Socket s, String name)
	{
		ClientInfo tmp = new ClientInfo(s, name);
		if (clients.contains(tmp))
		{
			clients.set(clients.indexOf(tmp), tmp);
		} else
		{
			clients.add(tmp);
		}
	}
	
	
	public void los() {
		clients = new CopyOnWriteArrayList<ClientInfo>();
		try {
			serverSock = new ServerSocket(port);
			System.out.printf("Server startet auf port %d...\n", port);
			while (	true) {
				System.out.println("Warte auf Client....");
				Socket clientSocket = serverSock.accept();
				System.out.println("Client meldet sich....");
				
				Thread t = new Thread(new ClientHandler(clientSocket, this));
				t.start();
				System.out.println("habe eine Verbindung...");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void beenden() throws IOException {
		serverSock.close();
	}

	public synchronized void buddyListSchicken() {
		String buddyString = "#BUDDY";
		for (ClientInfo client : clients) {
			buddyString += "," +  client.getName();
		}
		esAllenWeitersagen(buddyString);
	}

	public synchronized void esAllenWeitersagen(String nachricht) {
		Vector<ClientInfo> clientsToRemove = new Vector<ClientInfo>();
		
		Iterator<ClientInfo> it = clients.iterator();
		while (it.hasNext()) {
			try {
				ClientInfo info = (ClientInfo) it.next();
				PrintWriter writer = info.getWriter();
				
				writer.println(nachricht);
				if (writer.checkError())
				{
					clientsToRemove.add(info);
				}
				
				System.out.printf("Write to %s: %s (%s)\n", info.getName(), nachricht, writer.checkError());
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			for (ClientInfo ci : clientsToRemove)
			{
				clients.remove(ci);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		ChatServer s = new ChatServer();
		
		UpdateBuddyList buddyUpdate = new UpdateBuddyList(s);
		Timer t = new Timer();
		t.schedule(buddyUpdate, 3000, 5000);
		
		s.los();
		s.beenden();
	}
}
