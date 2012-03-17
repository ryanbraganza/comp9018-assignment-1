package flame;

/** 
 * Utilities for loading vertex and program shaders
 * @author not_ryanb
 */

import javax.media.opengl.*;
import java.io.*;

public class ShaderUtil {

	public static int makeShaders(GL gl, String shaderDir, String name) {

		String vShaderSource = readFile(shaderDir + File.separator + name
				+ ".vert");
		String fShaderSource = readFile(shaderDir + File.separator + name
				+ ".frag");
		int program = -1;
		if (fShaderSource != null && vShaderSource != null) {

			// compile vertex shader
			int vshader = gl.glCreateShader(GL.GL_VERTEX_SHADER);
			gl.glShaderSource(vshader, 1, new String[] { vShaderSource },
					(int[]) null, 0);
			gl.glCompileShader(vshader);
			printShaderInfoLog(gl, vshader);

			// compile fragment shader
			int fshader = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
			gl.glShaderSource(fshader, 1, new String[] { fShaderSource },
					(int[]) null, 0);
			gl.glCompileShader(fshader);
			printShaderInfoLog(gl, fshader);

			program = gl.glCreateProgram();

			gl.glAttachShader(program, vshader);
			gl.glAttachShader(program, fshader);
			gl.glLinkProgram(program);
			gl.glValidateProgram(program);
			printProgramInfoLog(gl, program);

		}
		return program;

	}

	public static void printShaderInfoLog(GL gl, int obj) {
		int[] iVal = new int[1];
		gl.glGetShaderiv(obj, GL.GL_INFO_LOG_LENGTH, iVal, 0);

		int length = iVal[0];

		if (length <= 1) {
			return;
		}

		byte[] infoLog = new byte[length];

		gl.glGetShaderInfoLog(obj, length, iVal, 0, infoLog, 0);

		System.out.println("Shader info log >> " + new String(infoLog));
	}

	public static void printProgramInfoLog(GL gl, int obj) {
		int[] iVal = new int[1];
		gl.glGetProgramiv(obj, GL.GL_INFO_LOG_LENGTH, iVal, 0);

		int length = iVal[0];

		if (length <= 1) {
			return;
		}

		byte[] infoLog = new byte[length];

		gl.glGetProgramInfoLog(obj, length, iVal, 0, infoLog, 0);

		System.out.println("Program info log >> " + new String(infoLog));
	}

	// list shaders -- all .vert files in the shader directory.
	public static String[] listShaders(String shaderDir) {
		File dir = new File(shaderDir);
		String[] names = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".vert");
			}
		});
		return names;
	}

	// read file into a string
	private static String readFile(String name) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(name)));
			StringWriter writer = new StringWriter();

			String line = reader.readLine();
			while (line != null) {
				writer.write(line);
				writer.write("\n");
				line = reader.readLine();
			}

			return writer.getBuffer().toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
