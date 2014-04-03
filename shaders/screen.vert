//#version 420
#version 330 compatibility

uniform float time;
out vec4 color;

float pi = 3.14159265358979323846264;

void main()
{
    vec4 v = vec4(gl_Vertex);
    float phase = (sin(time) + 1) / 2;
    vec4 c = vec4(phase, 1 - (phase), 1, 0.5);
    color = c;

    gl_Position = gl_ModelViewProjectionMatrix * v;
}
