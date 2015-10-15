attribute vec3 a_Position;
attribute vec3 a_Color;
uniform mat4 u_Matrix;
const float scale = 1.0;

varying vec4 v_Color;


void main() {
    v_Color = vec4(a_Color, 1.0);
    gl_Position = u_Matrix * (vec4(scale * a_Position, 1.0));
    gl_PointSize = 1.0;
}