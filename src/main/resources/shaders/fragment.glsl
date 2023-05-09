#version 430 core

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
uniform sampler2D textureSampler;

void main() {
    vec4 lightColor = vec4(1,1,1,1);

    float ambient = 0.5f;

    vec3 normal = normalize(fragNormal);

    float diffuse = max(dot(normal, lightVec), 0.4f);

    vec4 textureColor = texture(textureSampler, fragTextureCoord);
    if(textureColor.a < 0.5) {
        discard;
    }

    vec4 result = textureColor * lightColor * (diffuse + ambient);
    fragColor = result;
}