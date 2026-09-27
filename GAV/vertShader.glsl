#version 330 core

// incoming vertex position
layout (location = 0) in vec2 aPosition;

// push position straight to clip space
void main() {
    gl_Position = vec4(aPosition, 0.0, 1.0);
}
