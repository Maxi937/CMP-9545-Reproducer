package panels

import vscode.IDisposable
import vscode.Uri
import vscode.ViewColumn
import vscode.Webview
import vscode.WebviewPanel
import vscode.window.createWebviewPanel

class MyWebViewPanel(
    private val panel: WebviewPanel,
    private val extensionUri: Uri
) {
    private val disposables = mutableListOf<IDisposable>()
    private val webview: Webview get() = this.panel.webview

    init {
        this.panel.onDidDispose(
            listener = { dispose() },
            disposables = disposables.toTypedArray()
        )

        webview.html = getWebviewContent()
    }

    fun dispose() {
        currentPanel = null
        panel.dispose()
        disposables.forEach {
            it.dispose()
        }
    }

    private fun getNonce(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..32)
            .map { possible.random() }
            .joinToString("")
    }

    private fun getUri(vararg path: String): Uri {
        return webview.asWebviewUri(Uri.joinPath(extensionUri, *path))
    }

    private fun getWebviewContent(): String {
        val assetUri = getUri("kotlin", "ui")
        val scriptUri = getUri("kotlin", "ui", "ui.js")
        val stylesUri = getUri("kotlin", "ui", "styles.css")
        val webviewCspSource = webview.cspSource
        val nonce = getNonce()

        // we might make this a resource instead of raw here
        return """
        <!DOCTYPE html>
        <html lang="en">
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <meta http-equiv="Content-Security-Policy" content="
              default-src 'none';
              img-src $webviewCspSource https:;
              style-src $webviewCspSource 'nonce-$nonce';
              script-src $webviewCspSource 'nonce-$nonce' 'unsafe-eval';
              worker-src $webviewCspSource blob:;
              connect-src $webviewCspSource;
            " />
            <link rel="stylesheet" type="text/css" href="$stylesUri">
            <title>Dockli</title>
          </head>
          <body>
          <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 50 50" role="presentation">
              <circle cx="25" cy="25" r="20" stroke="#ccc" stroke-width="4" fill="none"/>
              <circle cx="25" cy="25" r="20" stroke="#333" stroke-width="4" fill="none" stroke-linecap="round"
                      stroke-dasharray="90 125">
                  <animateTransform attributeName="transform" type="rotate" from="0 25 25" to="360 25 25" dur="1s"
                                    repeatCount="indefinite"/>
              </circle>
          </svg>
            <script nonce="$nonce">
              window.__CSP_NONCE__ = "$nonce";
              
              const ASSET_BASE = "$assetUri";

              const originalFetch = window.fetch.bind(window);
            
              window.fetch = (input, init) => {
                if (typeof input === "string" && input.startsWith("./composeResources/")) {
                  input = ASSET_BASE + "/" + input.substring(2);
                }
                return originalFetch(input, init);
              };
            </script>
            <script type="module" nonce="$nonce" src="$scriptUri" defer></script>
          </body>
        </html>
        """.trimIndent()
        // fetch function override is just to do with overriding the path for asset requests
        // compose will use fetch with relative paths and we just need to override so the paths to the assets are correct
    }

    companion object {
        var currentPanel: MyWebViewPanel? = null

        fun create(extensionUri: Uri) {
            if (currentPanel != null) {
                currentPanel!!.panel.reveal(ViewColumn.One)
            } else {
                val panel = createWebviewPanel(
                    viewType = "helloWorld",
                    title = "My Cool Extension",
                    showOptions = ViewColumn.Two,
                    options = MyWebViewPanelOptions(
                        localResourceRoots = arrayOf(
                            Uri.joinPath(extensionUri, "kotlin", "ui"),
                            Uri.joinPath(extensionUri, "kotlin", "ui")
                        )
                    )
                )

                currentPanel = MyWebViewPanel(panel, extensionUri)
            }
        }
    }
}
