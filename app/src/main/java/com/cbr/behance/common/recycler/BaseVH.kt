package com.cbr.behance.common.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer


abstract class BaseVH(
        override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {


}