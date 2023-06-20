#version 410 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;
in vec3 lightVec;

out vec4 fragColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectability;
};

uniform Material material;
uniform float power;
uniform sampler2D textureSampler;

void main() {
    vec4 lightColor = vec4(1,1,1,1);
    vec3 lowColor = vec3(1.0, 1.0, 0.0);
    vec3 highColor = vec3(1.0, 0.0, 0.0);

    float ambient = 0.5f;

    vec3 normal = normalize(fragNormal);

    float diffuse = max(dot(normal, lightVec), 0.4f);

    float powerPercentage = power/500.0;
    vec4 textureColor = texture(textureSampler, fragTextureCoord);
    textureColor = vec4(mix(lowColor, highColor, powerPercentage), textureColor.a);
    if(textureColor.a < 0.5) {
        discard;
    }

    vec4 result = textureColor * lightColor * (diffuse + ambient);
    fragColor = result;
}