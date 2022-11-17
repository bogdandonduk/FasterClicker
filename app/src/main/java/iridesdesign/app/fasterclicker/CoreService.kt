package iridesdesign.app.fasterclicker

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout

class CoreService : AccessibilityService() {
  private var shareRideTab: Boolean = false

  private val viewIdTime = "sinet.startup.inDriver:id/time"
  private val viewIdClientLayout = "sinet.startup.inDriver:id/client_layout"
  private val viewIdOrderListItemLayout = "sinet.startup.inDriver:id/order_list_item_layout"

  private var lastTimeElementText: String? = null

  override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    event ?: return

    if (event.packageName != "sinet.startup.inDriver")
      return

    val contentRoot = event.source ?: return
    val windowContentRoot = contentRoot.window?.root ?: return

    val allNodes = windowContentRoot.allChildrenFlat

    if (!shareRideTab) {
      if (lastTimeElementText == null) {
        val latestOrderTime = allNodes.find {
          it.viewIdResourceName == viewIdTime
        }

        latestOrderTime?.apply {
          if (text != null)
            lastTimeElementText = text.toString()
        }
      } else {
        for (i in allNodes.indices) {
          val node = allNodes[i]

          val id = node.viewIdResourceName ?: continue
          val className = node.className ?: continue
          val text = node.text?.toString() ?: continue

          if (id == viewIdTime) {
            if (text == lastTimeElementText) {
              windowContentRoot
                .allChildrenFlat
                .find { it.className == "android.widget.LinearLayout" && it.contentDescription == "Share a ride" }
                ?.apply {
                  performAction(AccessibilityNodeInfo.ACTION_CLICK)

                  shareRideTab = true
                }

              break
            } else {
              val precedingElementsReversed = allNodes.subList(0, i).reversed()

              val clickTargetElement = precedingElementsReversed.find {
                it.className == FrameLayout::class.java.name
              } ?: continue

              clickTargetElement.performAction(AccessibilityNodeInfo.ACTION_CLICK)
              lastTimeElementText = text

              break
            }
          }
        }
      }
    } else
      windowContentRoot
        .allChildrenFlat
        .find { it.className == "android.widget.LinearLayout" && it.contentDescription == "Ride requests" }
        ?.apply {
          performAction(AccessibilityNodeInfo.ACTION_CLICK)

          shareRideTab = false
        }

//    sinet.startup.inDriver:id/time
//    sinet.startup.inDriver:id/order_list_item_layout
//    sinet.startup.inDriver:id/client_layout
//      if (accessibilityNodeInfo.className != null && accessibilityNodeInfo.className.contains("FrameLayout"))
//      if (accessibilityNodeInfo.viewIdResourceName != null && accessibilityNodeInfo.viewIdResourceName.toString().contains("sinet.startup.inDriver:id/main_layout"))
//        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
  }

  override fun onInterrupt() {}
}