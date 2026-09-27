package mod2ct;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.common.nio.Buffers;

public class Code implements GLEventListener {

    private int renderingProgram;
    private int vao[] = new int[1];
    private int vbo[] = new int[1];

    // holds all gasket vertices
    private float[] vertices;

    // keeps track of where we are writing into the array
    private int index = 0;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // builds all gasket vertices using recursion level 5
        buildGasketVertices();

        // loads shaders
        renderingProgram = Utils.createShaderProgram(gl, "vertShader.glsl", "fragShader.glsl");
        gl.glUseProgram(renderingProgram);

        // makes vao
        gl.glGenVertexArrays(1, vao, 0);
        gl.glBindVertexArray(vao[0]);

        // makes vbo
        gl.glGenBuffers(1, vbo, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);

        // sends vertex data to gpu
        int sizeInBytes = vertices.length * Float.BYTES;
        gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeInBytes, Buffers.newDirectFloatBuffer(vertices), GL.GL_STATIC_DRAW);

        // tells opengl how to read the vertex data
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 2, GL.GL_FLOAT, false, 0, 0);

        // sets background color
        gl.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // clears the screen
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // uses shader program
        gl.glUseProgram(renderingProgram);

        // binds vertex array
        gl.glBindVertexArray(vao[0]);

        //draws all triangles
        int numVertices = vertices.length / 2;
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, numVertices);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // updates viewport when window changes size
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    // builds the full gasket vertex array
    private void buildGasketVertices() {

        // base triangle points
        float[] a = { -0.8f, -0.8f };
        float[] b = {  0.8f, -0.8f };
        float[] c = {  0.0f,  0.8f };

        // recursion level 5
        int level = 5;
        int numTriangles = (int) Math.pow(3, level);

        // each triangle has 3 vertices, each vertex has 2 floats
        vertices = new float[numTriangles * 3 * 2];

        // starts writing at index 0
        index = 0;

        // recursively subdivide
        subdivide(a, b, c, level);
    }

    // recursive subdivision
    private void subdivide(float[] a, float[] b, float[] c, int level) {

        // base case no more subdivision
        if (level == 0) {
            // writes triangle vertices into array
            addVertex(a);
            addVertex(b);
            addVertex(c);
            return;
        }

        // finds the midpoints
        float[] ab = midpoint(a, b);
        float[] ac = midpoint(a, c);
        float[] bc = midpoint(b, c);

        // subdivides into 3 smaller triangles
        subdivide(a, ab, ac, level - 1);
        subdivide(ab, b, bc, level - 1);
        subdivide(ac, bc, c, level - 1);
    }

    // computes midpoint helper
    private float[] midpoint(float[] p, float[] q) {
        return new float[] {
            (p[0] + q[0]) / 2f,
            (p[1] + q[1]) / 2f
        };
    }

    // writes a single vertex into the array
    private void addVertex(float[] v) {
        vertices[index++] = v[0];
        vertices[index++] = v[1];
    }

    public static void main(String[] args) {

        // picks a modern opengl profile
        GLProfile profile = GLProfile.get(GLProfile.GL3);

        // sets up window capabilities
        GLCapabilities caps = new GLCapabilities(profile);

        //makes newt window
        GLWindow window = GLWindow.create(caps);

        // attaches our event listener
        window.addGLEventListener(new Code());

        // sets window size and title
        window.setSize(800, 800);
        window.setTitle("Sierpinski Gasket Fractal: Recursion Level 5 ");

        //shows the window
        window.setVisible(true);
        
     // keeps the window open until user closes it
        while (window.isVisible()) {
            window.display();
        }

    }
}
