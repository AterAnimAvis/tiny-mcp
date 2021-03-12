Tiny-MCP
---
### MCP Mappings to Yarn (Tiny v1)

Generates Tiny Mappings based on MCP Mappings Releases

The source code for this project is based on [Devoldefy](https://github.com/Runemoro/Devoldefy) by [Runemoro](https://github.com/Runemoro/)

### **Warning: This is *very dumb*, Use at your own risk**

### Usage

Edit `gradle.properties` and then run `gradlew install` to install the generated mappings to your local maven

You can disable the generation of TinyV1 or TinyV2 mappings by changing the appropriate `generateV` in the `gradle.properties` 

Add the local repository to your `build.gradle.kts`
```kotlin
repositories {
    mavenLocal {
        content { includeGroup("net.fabricmc") }
    }
}
```

Change your mappings dependency like so
```kotlin
dependencies {
    "mappings"(group = "net.fabricmc", name = "yarn", version = "$yarnVersion+build.$yarnBuild.mcp.$mappingsChannel.${mappingsVersion.replace("-", ".")}")
}
```

### Example

For Example with the following `gradle.properties`
```properties
mappingsChannel=snapshot
mappingsVersion=20201028-1.16.3
yarnVersion=1.16.5
yarnBuild=1
```

The generated dependency would have a version of `1.16.5+build.1.mcp.snapshot.20201028.1.16.3`