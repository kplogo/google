package service;

/**
 * Created by Krzysztof on 30.11.2015.
 */
public class Mode {
	private Type type;
	private int id;

	public Mode(int modeId, Type type) {
		this.id = modeId;
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public enum Type {
		NORMAL, IGNORE_SINGLE, IGNORE_START, IGNORE_STOP, IGNORE;
	}
}
