package com.electrodiux.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class Shader {

    private String filePath;

    private int vertexID, fragmentID, shaderProgram;

    private boolean beingUsed = false;

    public static Shader loadShader(String filePath) throws IOException {
        if (!filePath.startsWith("/"))
            filePath = "/" + filePath;

        InputStream in = Shader.class.getResourceAsStream(filePath);

        Shader shader = new Shader(filePath);
        shader.load(in);

        return shader;
    }

    public static Shader loadShader(InputStream in) throws IOException {
        Shader shader = new Shader("stream-path/" + Integer.toHexString(in.hashCode()));
        shader.load(in);

        return shader;
    }

    private Shader(String filePath) {
        this.filePath = filePath;
    }

    private void load(InputStream stream) throws IOException {
        try {
            String src = getSouceOfStream(stream);
            String[] shaderSrc = src.split("(#type)( )+([a-zA-z]+)");

            int index = 0;
            int eol = 0;

            String vertexSrc = null, fragmentSrc = null;

            for (int i = 1; i < shaderSrc.length; i++) {
                index = src.indexOf("#type", eol) + 6;
                eol = src.indexOf("\n", index);
                String type = src.substring(index, eol).trim();

                switch (type) {
                    case "vertex":
                        vertexSrc = shaderSrc[i];
                        break;
                    case "fragment":
                        fragmentSrc = shaderSrc[i];
                        break;
                    default:
                        throw new IOException("Unexpected token '" + type + "'");
                }
            }

            if (vertexSrc == null || fragmentSrc == null) {
                throw new IOException("Fragment or Vertex is null");
            }

            compile(vertexSrc, fragmentSrc);

        } catch (IOException e) {
            throw new IOException("Could not open file for shader: '" + this.filePath + "'", e);
        }
    }

    private String getSouceOfStream(InputStream stream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));

        String line = "";
        String data = "";

        while ((line = in.readLine()) != null) {
            data += line + "\n";
        }
        in.close();

        return data;
    }

    private void compile(String vertexSrc, String fragmentSrc) throws IllegalStateException {
        vertexID = compileShader(GL20.GL_VERTEX_SHADER, vertexSrc,
                "Vertex shader at: '" + filePath + "' compilation failed.");
        fragmentID = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSrc,
                "Fragment shader at: '" + filePath + "' compilation failed.");

        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexID);
        GL20.glAttachShader(shaderProgram, fragmentID);
        GL20.glLinkProgram(shaderProgram);

        int success = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH);
            throw new IllegalStateException(
                    "Linking of shaders failed: " + GL20.glGetProgramInfoLog(shaderProgram, len));
        }
    }

    private int compileShader(int shaderType, String src, String errorMessage) throws IllegalStateException {
        int success;
        int id;

        id = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(id, src);
        GL20.glCompileShader(id);

        success = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(id, GL20.GL_INFO_LOG_LENGTH);
            throw new IllegalStateException(errorMessage + "\n" + GL20.glGetShaderInfoLog(id, len));
        }

        return id;
    }

    public void setMatrix4f(String varName, Matrix4f matrix) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        FloatBuffer matrixBuff = BufferUtils.createFloatBuffer(16); // 4 * 4 matrix
        matrix.get(matrixBuff);
        GL20.glUniformMatrix4fv(varLocation, false, matrixBuff);
    }

    public void setMatrix3f(String varName, Matrix3f matrix) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        FloatBuffer matrixBuff = BufferUtils.createFloatBuffer(9); // 3 * 3 matrix
        matrix.get(matrixBuff);
        GL20.glUniformMatrix3fv(varLocation, false, matrixBuff);
    }

    public void setMatrix2f(String varName, Matrix2f matrix) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        FloatBuffer matrixBuff = BufferUtils.createFloatBuffer(4); // 2 * 2 matrix
        matrix.get(matrixBuff);
        GL20.glUniformMatrix2fv(varLocation, false, matrixBuff);
    }

    public void setVector4f(String varName, Vector4f vec) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        GL20.glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void setColor(String varName, Color value) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        GL20.glUniform4f(varLocation, value.r(), value.g(), value.b(), value.a());
    }

    public void setVector3f(String varName, Vector3f vec) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        GL20.glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void setVector2f(String varName, Vector2f vec) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        GL20.glUniform2f(varLocation, vec.x, vec.y);
    }

    public void setFloat(String varName, float value) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        GL20.glUniform1f(varLocation, value);
    }

    public void setInt(String varName, int value) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        GL20.glUniform1i(varLocation, value);
    }

    public void setBoolean(String varName, boolean value) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        GL20.glUniform1i(varLocation, value ? 1 : 0);
    }

    public void setIntArray(String varName, int[] values) {
        int varLocation = GL20.glGetUniformLocation(shaderProgram, varName);
        use();
        GL20.glUniform1iv(varLocation, values);
    }

    public void setTexture(int textureID) {
        setInt("texture", textureID);
    }

    public void setTexture(Texture texture) {
        setTexture(texture.getTextureId());
    }

    public void use() {
        if (!beingUsed) {
            GL20.glUseProgram(shaderProgram);
            beingUsed = true;
        }
    }

    public void detach() {
        GL20.glUseProgram(0);
        beingUsed = false;
    }

    public void destroy() {
        detach();
        GL20.glDetachShader(shaderProgram, vertexID);
        GL20.glDeleteShader(vertexID);
        GL20.glDetachShader(shaderProgram, fragmentID);
        GL20.glDeleteShader(fragmentID);
        GL20.glDeleteProgram(shaderProgram);
    }

    public int getShaderProgramID() {
        return shaderProgram;
    }

    public int getVertexID() {
        return vertexID;
    }

    public int getFragmentID() {
        return fragmentID;
    }

}
