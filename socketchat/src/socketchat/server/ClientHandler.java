package socketchat.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
	Socket clientSocket;
	ChatServer server;

	
	public ClientHandler(Socket clientSocket, ChatServer server) {
		this.clientSocket = clientSocket;
		this.server = server;
	}

	public void run() {
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			while (true)
			{
				String msg = reader.readLine();
				if (msg == null)
					continue;
				
				if (msg.startsWith("#B"))
				{
					String name = msg.substring(2);
					server.setClient(clientSocket, name);
				} else
				{
					server.esAllenWeitersagen(msg);	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}