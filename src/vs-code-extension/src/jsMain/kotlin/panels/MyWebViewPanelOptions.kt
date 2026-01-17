package panels

import js.array.ReadonlyArray
import vscode.Uri
import vscode.WebviewPortMapping
import vscode.window.CreateWebviewPanelOptions

data class MyWebViewPanelOptions(
    override val enableFindWidget: Boolean = false,
    override val retainContextWhenHidden: Boolean = false,
    override val enableCommandUris: Boolean = false,
    override val enableForms: Boolean = true,
    override val enableScripts: Boolean = true,
    override val localResourceRoots: ReadonlyArray<Uri> = emptyArray<Uri>(),
    override val portMapping: ReadonlyArray<WebviewPortMapping> = emptyArray<WebviewPortMapping>()
) : CreateWebviewPanelOptions {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as MyWebViewPanelOptions

        if (enableFindWidget != other.enableFindWidget) return false
        if (retainContextWhenHidden != other.retainContextWhenHidden) return false
        if (enableForms != other.enableForms) return false
        if (enableScripts != other.enableScripts) return false
        if (enableCommandUris != other.enableCommandUris) return false
        if (!localResourceRoots.contentEquals(other.localResourceRoots)) return false
        if (!portMapping.contentEquals(other.portMapping)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enableFindWidget.hashCode()
        result = 31 * result + (retainContextWhenHidden.hashCode())
        result = 31 * result + (enableForms.hashCode())
        result = 31 * result + (enableScripts.hashCode())
        result = 31 * result + (enableCommandUris.hashCode())
        result = 31 * result + (localResourceRoots.contentHashCode())
        result = 31 * result + (portMapping.contentHashCode())
        return result
    }
}
