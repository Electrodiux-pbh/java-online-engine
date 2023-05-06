#type vertex
#version 330 core

layout(location=0)in vec3 aPos;
layout(location=1)in vec2 aTextCoords;
layout(location=2)in vec3 aNormal;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 transformMatrix;

out vec2 fTextCoords;
out vec3 surfaceNormal;

// Global light
uniform vec3 globalLightDirection;
out vec3 globalLightNormal;

// Spot lights
const int maxSpotlights=10;
uniform vec3 spotLightsPositions[maxSpotlights];
out vec3 spotLightsVectors[maxSpotlights];

void main()
{
	vec4 vertexPosition=transformMatrix*vec4(aPos,1.);
	
	fTextCoords=aTextCoords;
	gl_Position=uProjection*uView*vertexPosition;
	
	// Lightning
	surfaceNormal=normalize((transformMatrix*vec4(aNormal,0.)).xyz);
	globalLightNormal=normalize(-globalLightDirection);
	
	for(int i=0;i<maxSpotlights;i++){
		spotLightsVectors[i]=spotLightsPositions[i]-vertexPosition.xyz;
	}
}

#type fragment
#version 330 core

in vec2 fTextCoords;

uniform sampler2D uSampler;

uniform bool usingTexture;
uniform vec4 inColor;

// Light
in vec3 surfaceNormal;

// Global light
const float minimumLightDiffuse=.2;
in vec3 globalLightNormal;
uniform vec4 globalLightColor;

// Spot lights
const int maxSpotlights=10;
uniform vec4 spotLightsColors[maxSpotlights];
uniform vec3 spotLightsAttenuations[maxSpotlights];
in vec3 spotLightsVectors[maxSpotlights];

out vec4 out_color;

void main()
{
	
	// Global light
	float globalBrightness=max(dot(surfaceNormal,globalLightNormal),0);
	
	vec4 totalDiffuse=globalBrightness*globalLightColor;
	
	// Spot lights
	for(int i=0;i<maxSpotlights;i++){
		float distance=length(spotLightsVectors[i]);
		
		vec3 attenuation=spotLightsAttenuations[i];
		float attFactor=attenuation.x+(attenuation.y*distance)+(attenuation.z*distance*distance);
		
		float nDotl=dot(surfaceNormal,normalize(spotLightsVectors[i]));
		float brightness=max(nDotl,0);
		
		totalDiffuse=totalDiffuse+(brightness*spotLightsColors[i])/attFactor;
		// /attFactor Same for specular light
	}
	
	totalDiffuse=max(totalDiffuse,minimumLightDiffuse);
	
	if(usingTexture){
		vec4 textureColor=texture(uSampler,fTextCoords);
		out_color=totalDiffuse*textureColor;
	}else{
		out_color=totalDiffuse*inColor;
	}
}