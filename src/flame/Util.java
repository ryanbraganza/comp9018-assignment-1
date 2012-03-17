package flame;

import java.awt.Dimension;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;

public class Util {
	// draw all the points using DrawArrays
	public static void drawPoints(GL gl, FloatBuffer verts, FloatBuffer colors,
			FloatBuffer velocities, int VELOCITY_ARRAY, FloatBuffer startTimes,
			int START_TIME_ARRAY, Dimension arraySize) {
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
		
		gl.glDrawArrays(GL.GL_POINTS, 0, arraySize.width* arraySize.height);

		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDisableVertexAttribArray(VELOCITY_ARRAY);
		gl.glDisableVertexAttribArray(START_TIME_ARRAY);

	}
}
