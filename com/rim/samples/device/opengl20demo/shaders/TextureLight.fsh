precision mediump float;

uniform vec3 lightAmbient; // Ambient light.
uniform vec3 lightDiffuse; // Diffuse light.
uniform sampler2D texture;

varying vec2 v_texCoord; // Texture coordinates
varying vec3 v_normal;   // Transformed normal
varying vec3 v_lightDir; // Light direction

void main(void)
{
	// Get the base color of the fragment from the texture.
    vec3 baseColor = vec3(texture2D(texture, v_texCoord));
    
	// Dot product of normal and light direction
    float df = max(0.0, dot((v_normal), (v_lightDir)));

	// Adjust the color based on the color and intensity of the ambient and diffuse light properties.
    vec3 color = lightAmbient * baseColor + df * lightDiffuse * baseColor;
	
	// Set the fragment output color.
    gl_FragColor = vec4(color, 1.0);
}
