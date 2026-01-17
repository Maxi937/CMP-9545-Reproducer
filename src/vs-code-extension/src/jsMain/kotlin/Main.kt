@file:OptIn(ExperimentalJsExport::class)

import panels.MyWebViewPanel
import vscode.ExtensionContext
import vscode.commands.registerCommand

// The entry point for the extension.
//
// We must use JsExport as VS Code looks for top level
// functions specifically named "activate" and "deactivate"

@JsExport
fun activate(context: ExtensionContext) {
    println("Extension activated!")

    val disposable = registerCommand("helloworld.helloWorld", {
        MyWebViewPanel.create(context.extensionUri)
    })

    context.subscriptions.add(disposable)
}

@JsExport
fun deactivate() {
    println("Extension deactivated!")
}
