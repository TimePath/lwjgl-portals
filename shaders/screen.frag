/*
varying vec4 vertColor;

void main() {
    gl_FragColor = vertColor;
}
*/

#version 330 compatibility
in vec4 color;
out vec4 my_FragColor; // assigned to position 0

void main()
{
	my_FragColor = color;
}
