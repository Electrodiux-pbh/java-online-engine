#type vertex
#version 330 core

layout(location=0)in vec3 aPos;
layout(location=1)in vec2 aTextCoords;
layout(location=2)in vec3 aNormal;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 transformMatrix;

uniform vec3 lightDirection;

out vec2 fTextCoords;
out vec3 surfaceNormal;
out vec3 toLightVector;

void main()
{
	vec4 vertexPosition=transformMatrix*vec4(aPos,1.);
	
	fTextCoords=aTextCoords;
	gl_Position=uProjection*uView*vertexPosition;
	
	surfaceNormal=(transformMatrix*vec4(aNormal,0.)).xyz;
	toLightVector=-lightDirection;
}

#type fragment
#version 330 core

in vec2 fTextCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;

uniform sampler2D uSampler;

uniform vec4 lightColor;

uniform bool usingTexture;
uniform vec4 inColor;

out vec4 color;

void main()
{
	vec3 unitNormal=normalize(surfaceNormal);
	vec3 unitLight=normalize(toLightVector);
	
	float nDot=dot(unitNormal,unitLight);
	float brightness=max(nDot,.1);
	
	vec4 diffuse=brightness*lightColor;
	
	if(usingTexture){
		color=diffuse*texture(uSampler,fTextCoords);
	}else{
		color=diffuse*inColor;
	}
}