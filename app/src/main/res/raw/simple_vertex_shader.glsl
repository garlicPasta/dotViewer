attribute vec3 a_Position;
attribute vec3 a_Color;
attribute float a_Size;
uniform mat4 u_Matrix;

varying vec4 v_Color;


void main() {
    v_Color = vec4(a_Color / 255.0, 1.0);
    gl_Position = u_Matrix * (vec4(a_Position, 1.0));
    //gl_PointSize = sqrt(a_Size);
    gl_PointSize = 5.0;
}