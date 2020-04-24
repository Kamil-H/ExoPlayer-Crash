package com.nomtek.exoplayercrash.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nomtek.exoplayercrash.R

class DividerDecorator : RecyclerView.ItemDecoration() {

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)

        val space = parent.context.resources.getDimension(R.dimen.recycler_view_divider).toInt()
        val color = ContextCompat.getColor(parent.context, R.color.divider)

        val paint = Paint()
        paint.color = color

        drawHorizontal(canvas, space, paint, parent)
    }

    private fun drawHorizontal(canvas: Canvas, space: Int, paint: Paint, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount - 1
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + space

            val rect = Rect(left, top, right, bottom)
            canvas.drawRect(rect, paint)

            if (i == 0) {
                val firstItemDecoratorRight = child.left - params.leftMargin
                val firstItemDecoratorLeft = firstItemDecoratorRight - space

                val firstItemDecoratorRect = Rect(firstItemDecoratorLeft, top, firstItemDecoratorRight, bottom)
                canvas.drawRect(firstItemDecoratorRect, paint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val space = parent.context.resources.getDimension(R.dimen.recycler_view_divider).toInt()

        outRect.bottom = space

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space
        }
    }
}
