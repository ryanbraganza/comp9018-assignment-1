package flame;

import javax.swing.JFrame;

public class Runner {
	public static void main (String[] args) {
		FlameShader fshader = FlameShader.makeFlameShader();
		fshader.setTitle("Flame Shader");
		fshader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fshader.setVisible(true);
	}
}
