
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    mainUIViewController = ComposeUIViewController { App() }
    return mainUIViewController
}

lateinit var mainUIViewController: UIViewController