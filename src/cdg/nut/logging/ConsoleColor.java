package cdg.nut.logging;

import cdg.nut.util.gl.GLColor;

public enum ConsoleColor {
	RESET("\u001B[0m", new GLColor(1.0f, 1.0f, 1.0f)),
	BLACK("\u001B[30m", new GLColor(0.0f, 0.0f, 0.0f)),
	RED("\u001B[31m", new GLColor(1.0f, 0.0f, 0.0f)),
	GREEN("\u001B[32m", new GLColor(0.0f, 1.0f, 0.0f)),
	YELLOW("\u001B[33m", new GLColor(1.0f, 1.0f, 0.0f)),
	BLUE("\u001B[34m", new GLColor(0.0f, 0.0f, 1.0f)),
	PURPLE("\u001B[35m", new GLColor(1.0f, 0.0f, 1.0f)),
	CYAN("\u001B[36m", new GLColor(0.0f, 1.0f, 1.0f)),
	WHITE("\u001B[37m", new GLColor(1.0f, 1.0f, 1.0f));

	private String ansiColor;
	private GLColor color;

	ConsoleColor(String ansi, GLColor color)
	{
		this.ansiColor = ansi;
		this.color = color;
	}

	public String getAnsiColor() {
		return ansiColor;
	}

	public GLColor getColor() {
		return color;
	}
}
