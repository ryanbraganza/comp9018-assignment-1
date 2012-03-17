package demo;

/** 
 * Particle shader from Orange Book in Java
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;

public class ParticleShader extends JFrame implements GLEventListener,
		KeyListener {

	private static final long serialVersionUID = 1L;
	Animator animator;
	Trackball ball = new Trackball();
	int aWidth = 400;
	int aHeight = 400;

	int program; // shader program
	int timeLoc; // location of time variable

	static final int VELOCITY_ARRAY = 1;
	static final int START_TIME_ARRAY = 2;

	public static void main(String[] args) {
		ParticleShader t = new ParticleShader();
		// exit if frame's close box is clicked
		t.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}

			public void windowClosing(WindowEvent e) {
				windowClosed(e);
			}
		});
	}

	public ParticleShader() {
		super("ParticleShader");

		GLCanvas glc = new GLCanvas();

		add("Center", glc);
		glc.setSize(aWidth, aHeight);

		ball.listen(glc);
		glc.addGLEventListener(this);
		glc.addKeyListener(this);
		addKeyListener(this);
		animator = new FPSAnimator(glc, 30); // animate at 30 fps
		animator.start();
		pack(); // work out sizes of all Components
		setVisible(true);
	}

	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		createPoints(100, 100);

		gl.glDepthFunc(GL.GL_LESS);
		gl.glEnable(GL.GL_DEPTH_TEST);

		// use vertex and fragment shaders
		program = ShaderUtil.makeShaders(gl, ".", "particle");

		gl.glBindAttribLocation(program, VELOCITY_ARRAY, "Velocity");
		gl.glBindAttribLocation(program, START_TIME_ARRAY, "StartTime");

		gl.glUseProgram(program);

		// Set up initial uniform values

		gl.glUniform4f(gl.glGetUniformLocation(program, "Background"), 0.0f,
				0.0f, 0.0f, 1.0f);
		gl.glUniform1f(gl.glGetUniformLocation(program, "Time"), -5.0f);

	}

	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyChar() == 'q') {
			animator.stop();
			System.exit(0);
		}
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
		aWidth = width;
		aHeight = height;

	}

	float ParticleTime = 0.0f;

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
		gl.glMultMatrixf(ball.getRotMatrix(), 0);
		drawPoints(gl);

		gl.glPopMatrix();
	}

	/** This method handles things if display depth changes */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	int arrayWidth, arrayHeight;
	FloatBuffer verts, colors, velocities, startTimes;

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
		gl.glPointSize(2.0f);

		verts.rewind();
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, verts);
		colors.rewind();
		gl.glColorPointer(3, GL.GL_FLOAT, 0, colors);
		velocities.rewind();
		gl.glVertexAttribPointer(VELOCITY_ARRAY, 3, GL.GL_FLOAT, false, 0,
				velocities);
		startTimes.rewind();
		gl.glVertexAttribPointer(START_TIME_ARRAY, 1, GL.GL_FLOAT, false, 0,
				startTimes);

		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
		gl.glEnableVertexAttribArray(VELOCITY_ARRAY);
		gl.glEnableVertexAttribArray(START_TIME_ARRAY);

		gl.glDrawArrays(GL.GL_POINTS, 0, arrayWidth * arrayHeight);

		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDisableVertexAttribArray(VELOCITY_ARRAY);
		gl.glDisableVertexAttribArray(START_TIME_ARRAY);

	}
	
	/*
	 * These functions are "callbacks" for events we are not interested in
	 */

	public void keyTyped(KeyEvent evt) {
	}

	public void keyReleased(KeyEvent evt) {
	}

}
