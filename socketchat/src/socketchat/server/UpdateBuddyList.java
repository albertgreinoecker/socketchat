package socketchat.server;

import java.util.TimerTask;

public class UpdateBuddyList extends TimerTask {

	ChatServer server;
	
	public UpdateBuddyList(ChatServer server) 
	{
		this.server = server;
	}
	@Override
	public void run() {
		server.buddyListSchicken();
		
	}

}
