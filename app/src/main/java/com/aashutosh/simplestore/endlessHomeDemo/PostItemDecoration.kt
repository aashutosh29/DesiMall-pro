
package com.aashutosh.simplestore.endlessHomeDemo

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView

class PostItemDecoration(private val space: Int, private val spanSizeLookup: SpanSizeLookup) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val position = parent.getChildLayoutPosition(view)
    if (spanSizeLookup.getSpanSize(position) == 1) {
      outRect.left = space
      if (position % 2 == 0) {
        outRect.right = space
      }
    }
  }
}
