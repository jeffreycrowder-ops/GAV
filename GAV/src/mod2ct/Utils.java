package mod2ct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.jogamp.opengl.GL3;

public class Utils {

    // reads shader file into a string
    //grabss the whole file at once
    private static String readShaderFile(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            throw new RuntimeException("error reading shader file: " + filename, e);
        }
    }

    // builds and links a shader program using vertex and fragment shaders
    public static int createShaderProgram(GL3 gl, String vShaderFile, String fShaderFile) {

        // loads shader source from files
        String vsrc = readShaderFile(vShaderFile);
        String fsrc = readShaderFile(fShaderFile);

        // makes shader objects
        int vShader = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);

        // sends vertex shader source to opengl
        String[] vLines = new String[] { vsrc };
        int[] vLengths = new int[] { vsrc.length() };
        gl.glShaderSource(vShader, 1, vLines, vLengths, 0);
        gl.glCompileShader(vShader);

        // sends fragment shader source to opengl
        String[] fLines = new String[] { fsrc };
        int[] fLengths = new int[] { fsrc.length() };
        gl.glShaderSource(fShader, 1, fLines, fLengths, 0);
        gl.glCompileShader(fShader);

        // link shaders to program
        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vShader);
        gl.glAttachShader(program, fShader);
        gl.glLinkProgram(program);

        // cleans up shader objects
        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);

        return program;
    }
}
