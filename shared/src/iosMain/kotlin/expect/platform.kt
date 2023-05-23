package expect

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ObjCAction
import model.AppPlatform
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventEditingChanged
import platform.UIKit.UITextBorderStyle
import platform.UIKit.UITextField

actual fun platform() = AppPlatform.IOS

@Composable
actual fun ChatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    label: String
) {
    val color = MaterialTheme.colorScheme.surfaceVariant

    val factory = remember {
        val textField = object : UITextField(CGRectMake(0.0, 0.0, 0.0, 0.0)) {
            @ObjCAction
            fun editingChanged() {
                onValueChange(text ?: "")
            }
        }
        textField.placeholder = (label)
        textField.setBorderStyle(UITextBorderStyle.UITextBorderStyleNone)
        textField.backgroundColor = UIColor(
            red = color.red.toDouble(),
            green = color.green.toDouble(),
            blue = color.blue.toDouble(),
            alpha = 1.0
        )
        textField.addTarget(
            target = textField,
            action = NSSelectorFromString(textField::editingChanged.name),
            forControlEvents = UIControlEventEditingChanged
        )
        textField
    }

    UIKitView(
        factory = {
            factory
        },
        modifier = modifier.height(40.dp)
    )

//    UIKitView(
//        factory = {
//            val textField = UITextView()/* {
//                @ObjCAction
//                fun editingChanged() {
//                    onValueChange(text ?: "")
//                }
//            }*/
////            textField.addTarget(
////                target = textField,
////                action = NSSelectorFromString(textField::editingChanged.name),
////                forControlEvents = UIControlEventEditingChanged
////            )
//            textField
//        },
//        modifier = modifier,
//        update = { textField ->
//            textField.text = value
//        },
////        onRelease = { textField ->
////            textField.removeTarget(
////                target = textField,
////                action = NSSelectorFromString(textField::editingChanged.name),
////                forControlEvents = UIControlEventEditingChanged
////            )
////        },
//    )
}

//
//internal val currentRootViewController by lazy {
//    ComposeRootController()
//}
//
//internal var onKeyboardOpen: ((Boolean) -> Unit)? = null
//
//fun getRootController() = currentRootViewController
//
//class ComposeRootController internal constructor(): UIViewController(null, null) {
//
//    private val keyboardVisibilityListener = object : NSObject() {
//        @Suppress("unused")
//        @ObjCAction
//        fun keyboardWillShow(arg: NSNotification) {
//            val (width, height) = getViewFrameSize()
//
//            view.setClipsToBounds(true)
//
//            composeView.layer.setBounds(
//                CGRectMake(
//                    x = 0.0,
//                    y = 0.0,
//                    width = width.toDouble(),
//                    height = height.toDouble()
//                )
//            )
//
//            onKeyboardOpen?.invoke(true)
//        }
//
//        @Suppress("unused")
//        @ObjCAction
//        fun keyboardWillHide(arg: NSNotification) {
//            val (width, height) = getViewFrameSize()
//            view.layer.setBounds(CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()))
//
//            onKeyboardOpen?.invoke(false)
//        }
//
//        @Suppress("unused")
//        @ObjCAction
//        fun keyboardDidHide(arg: NSNotification) {
//            view.setClipsToBounds(false)
//
//            onKeyboardOpen?.invoke(false)
//
//        }
//    }
//
//    private val composeView = Application(title = "Nek") {
//        // App()
//    }.view
//
//    override fun viewDidLoad() {
//        super.viewDidLoad()
//
//        this.view.addSubview(composeView)
//    }
//
//    private fun getViewFrameSize(): IntSize {
//        val (width, height) = view.frame().useContents { this.size.width to this.size.height }
//        return IntSize(width.toInt(), height.toInt())
//    }
//
//    override fun viewDidAppear(animated: Boolean) {
//        super.viewDidAppear(animated)
//        NSNotificationCenter.defaultCenter.addObserver(
//            observer = keyboardVisibilityListener,
//            selector = NSSelectorFromString("keyboardWillShow:"),
//            name = UIKeyboardWillShowNotification,
//            `object` = null
//        )
//
//        NSNotificationCenter.defaultCenter.addObserver(
//            observer = keyboardVisibilityListener,
//            selector = NSSelectorFromString("keyboardWillHide:"),
//            name = UIKeyboardWillHideNotification,
//            `object` = null
//        )
//    }
//}