package clientController;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface Strategy {
	
	public String sendMove();
	
}
