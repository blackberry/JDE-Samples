attribute vec4 position; // Vertex position
attribute vec2 texCoord; // Vertex texture coordinates
attribute vec3 normal;   // Vertex normal (normalized)

uniform mat4 matrix;          // The combined projection-modelview matrix.
uniform vec3 lightDirection;  // The direction of the light (normalized)

varying vec2 v_texCoord; // Texture coordinates
varying vec3 v_normal;   // Transformed normal
varying vec3 v_lightDir; // Light direction

void main(void)
{
    // Assign the texture cooridinate, normal and light direction to varyings.
    // The varyings will be linear interpolated across the primitive during rasterization.
    v_texCoord = texCoord;
    v_lightDir = lightDirection;
    // Transform the normal by the projection-modelview matrix.
    v_normal = normalize((matrix * vec4(normal, 0.0)).xyz);
    
    // Transform the position by the projection-modelview matrix.
    // Set the vertex output position.
    gl_Position = matrix * position;
}