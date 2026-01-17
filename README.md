# CMP-9545 Reproducer

This repository is a minimal reproducer for  
[CMP-9545 – Resources on Web not loading because Cache Storage API is not supported everywhere](https://youtrack.jetbrains.com/issue/CMP-9545/CMP-Resources-on-Web-not-loading-because-Cache-Storage-API-is-not-supported-everywhere).

The project demonstrates a VS Code extension using Compose Multiplatform where a WebView panel fails to load resources due to Cache Storage API usage.

## Requirements
- VS Code installed
- The `code` command available on `PATH` (default after installation; on Linux it may need to be enabled manually)
- Java 17 or newer
- Gradle (wrapper included)

## Steps to reproduce

1. Run the Gradle task: 
```bash
./gradlew vscode:debugExtension
```
2. Wait for the VS Code window to open.

3. In the new VS Code window, press `Ctrl+P` (`Cmd+P` on macOS) to open the command palette.

4. Run the command: **Hello World**

5. A WebView panel opens and renders with missing resources.

## Notes
- The kotlin-externals dependency provides Kotlin wrappers for the VS Code API.  
  Original credit belongs to Edoardo Luppi: https://github.com/lppedd/kotlin-externals  
  This project uses a fork published on JitPack for easier access.

- The resource loading issue can be fixed by downgrading Compose Multiplatform, without any code changes.

## Related issue
https://youtrack.jetbrains.com/issue/CMP-9545