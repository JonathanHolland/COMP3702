package fakeBrains;

@SuppressWarnings("serial")
public class NoSuchNodeException extends Exception {
	public NoSuchNodeException() {
		super();
	}

	public NoSuchNodeException(String message) {
		super(message);
	}
}
