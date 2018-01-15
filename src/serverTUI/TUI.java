package serverTUI;

import generalUI.*;

public class TUI implements MessageUI {

	// makes a new server, that server will show results on the TUI.
	// TUI will ask for a port, and will create a new server with that port.
	
	public void print(String message) {
		System.out.println(message);
	}
}
