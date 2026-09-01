package id.neo.hr.presentation.theme

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController

internal class StatusBarViewController(
  private val composeController: UIViewController,
) : UIViewController(nibName = null, bundle = null) {

  private var isLightStatusBar = true

  @OptIn(ExperimentalForeignApi::class)
  override fun viewDidLoad() {
    super.viewDidLoad()

    addChildViewController(composeController)
    composeController.view.setFrame(view.bounds)
    composeController.view.autoresizingMask =
      UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
    view.addSubview(composeController.view)
    composeController.didMoveToParentViewController(this)
  }

  override fun preferredStatusBarStyle(): UIStatusBarStyle =
    if (isLightStatusBar) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent

  fun setLightStatusBar(isLight: Boolean) {
    if (isLightStatusBar == isLight) return

    isLightStatusBar = isLight
    setNeedsStatusBarAppearanceUpdate()
  }
}
