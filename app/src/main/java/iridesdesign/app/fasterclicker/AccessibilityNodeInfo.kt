package iridesdesign.app.fasterclicker

import android.view.accessibility.AccessibilityNodeInfo

val AccessibilityNodeInfo.allChildrenFlat: List<AccessibilityNodeInfo>
  get() {
    val allChildrenFlat = mutableListOf<AccessibilityNodeInfo>()

    traverseChildrenTree(allChildrenFlat)

    return allChildrenFlat.toList()
  }

private fun AccessibilityNodeInfo.traverseChildrenTree(collectToList: MutableList<AccessibilityNodeInfo>) {
  collectToList.add(this)

  if (childCount == 0) return

  for (i in 0 until childCount)
    getChild(i)?.also {
      it.traverseChildrenTree(collectToList)
    }
}

fun AccessibilityNodeInfo.findAccessibilityNodeInfosByContentDescription(contentDescription: CharSequence): List<AccessibilityNodeInfo> {
  val matchingChildren = mutableListOf<AccessibilityNodeInfo>()

  matchingChildren.addAll(allChildrenFlat.filter { it.contentDescription == contentDescription })

  return matchingChildren.toList()
}



