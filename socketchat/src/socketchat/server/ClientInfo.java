package socketchat.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Für jeden User möchte man die grundsätzliche Info speichern
 *
 */
public class ClientInfo {
	private PrintWriter writer;
	private String name;
	private Socket socket;
	
	public ClientInfo(Socket socket, String name) {
		this.name = name;
		this.socket = socket;
		try {
			writer = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PrintWriter getWriter() {
		return writer;
	}


	public Socket getSocket() {
		return socket;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@Override
		public boolean equals(Object obj) {
			ClientInfo ci = (ClientInfo) obj;
			return socket == ci.getSocket();
		}
}
