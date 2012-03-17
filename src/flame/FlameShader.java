package flame;

/** 
 * Particle shader from Orange Book in Java
 */

import java.awt.Dimension;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;

public class FlameShader extends JFrame implements GLEventListener{
	private static final int FPS = 1000;

	private static final long serialVersionUID = 1L;
	private Animator animator;
	private float ParticleTime = 0.0f;

	private int program; // shader program
	private int timeLoc; // location of time variable

	private static final int VELOCITY_ARRAY = 1;
	private static final int START_TIME_ARRAY = 2;
	private static final int WIDTH=400;
	private static final int HEIGHT=300;
	
	int arrayWidth, arrayHeight;
	FloatBuffer verts, colors, velocities, startTimes;


	public FlameShader() {
		super("ParticleShader");
		this.setSize(new Dimension(WIDTH, HEIGHT));
		GLCanvas glc = new GLCanvas();

		add("Center", glc);

		glc.addGLEventListener(this);
		animator = new FPSAnimator(glc, FPS);
		animator.start();
		pack(); // work out sizes of all Components
		setVisible(true);
	}

	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		FloatBuffer b = FloatBuffer.allocate(2);
		gl.glGetFloatv(GL.GL_POINT_SIZE_RANGE, b);
		for (float f : b.array()) {
			System.out.println(f);
		}
		gl.glPointSize(10);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		createPoints(100, 100);

		gl.glDepthFunc(GL.GL_LESS);
		gl.glEnable(GL.GL_DEPTH_TEST);

		// use vertex and fragment shaders
		program = ShaderUtil.makeShaders(gl, ".", "flame");

		gl.glBindAttribLocation(program, VELOCITY_ARRAY, "Velocity");
		gl.glBindAttribLocation(program, START_TIME_ARRAY, "StartTime");

		gl.glUseProgram(program);

		// Set up initial uniform values

		gl.glUniform4f(gl.glGetUniformLocation(program, "Background"), 0.0f,
				0.0f, 0.0f, 1.0f);
		gl.glUniform1f(gl.glGetUniformLocation(program, "Time"), -5.0f);

	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL gl = drawable.getGL();
		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-2, 2, -2, 2, -5, 10);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

	}



	public void display(GLAutoDrawable drawable) {

		GL gl = drawable.getGL();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);

		// tell shader the time
		ParticleTime += 0.02f;

		if (ParticleTime > 15.0)
			ParticleTime = 0.0f;

		timeLoc = gl.glGetUniformLocation(program, "Time");
		gl.glUniform1f(timeLoc, ParticleTime);

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -5.0f);
		drawPoints(gl);

		gl.glPopMatrix();
	}


	void createPoints(int w, int h) {
		float x, z;

		verts = BufferUtil.newFloatBuffer(w * h * 3);
		colors = BufferUtil.newFloatBuffer(w * h * 3);
		velocities = BufferUtil.newFloatBuffer(w * h * 3);
		startTimes = BufferUtil.newFloatBuffer(w * h);
		

		for (x = 0.5f / w - 0.5f; x < 0.5f; x = x + 1.0f / w)
			for (z = 0.5f / h - 0.5f; z < 0.5f; z = z + 1.0f / h) {
				verts.put(x);
				verts.put(0.0f);
				verts.put(z);

				colors.put((float) (Math.random() * 0.5 + 0.5));
				colors.put((float) (Math.random() * 0.5 + 0.5));
				colors.put((float) (Math.random() * 0.5 + 0.5));

				velocities.put((float) (Math.random() + 3.0));
				velocities.put((float) (Math.random() * 10.0));
				velocities.put((float) (Math.random() + 3.0));

				startTimes.put((float) (Math.random() * 10.0));

			}

		arrayWidth = w;
		arrayHeight = h;
	}

	// draw all the points using DrawArrays
	void drawPoints(GL gl) {
		Util.drawPoints(gl, verts, colors, velocities, VELOCITY_ARRAY, startTimes, START_TIME_ARRAY, new Dimension(arrayWidth, arrayHeight));
	}
	
	public static FlameShader makeFlameShader() {
		return new FlameShader();
		
	}
	
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

}
