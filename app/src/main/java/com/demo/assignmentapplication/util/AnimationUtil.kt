package com.demo.assignmentapplication.util

import android.animation.ValueAnimator
import android.view.View

object AnimationUtil {
    fun expandView(view: View, duration: Long = 300) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
        }
        animator.duration = duration
        animator.start()
    }

    fun collapseView(view: View, duration: Long = 300) {
        val initialHeight = view.measuredHeight

        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
            if (value == 0) {
                view.visibility = View.GONE
            }
        }
        animator.duration = duration
        animator.start()
    }
}