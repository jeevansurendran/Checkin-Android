package com.checkin.app.checkin.Menu.UserMenu.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Menu.Model.OrderedItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.session.model.TrendingDishModel
import com.checkin.app.checkin.Utility.DebouncedOnClickListener
import java.util.ArrayList

class MenuBestSellerAdapter(private val mListener: SessionTrendingDishInteraction?) : RecyclerView.Adapter<MenuBestSellerAdapter.ViewHolder>() {
    private var mItems: List<TrendingDishModel>? = null
    private lateinit var mHolder: ViewHolder

    fun setData(newData: List<TrendingDishModel>) {
//        this.mItems = data
//        notifyDataSetChanged()
        if (mItems != null) {
            val postDiffCallback = PostDiffCallback(mItems!!, newData)
            val diffResult = DiffUtil.calculateDiff(postDiffCallback)

            var mItems: MutableList<TrendingDishModel> = mutableListOf<TrendingDishModel>()

            mItems!!.clear()
            mItems!!.addAll(newData)
            diffResult.dispatchUpdatesTo(this)
        }else{
            // first initialization
            mItems = newData
//            notifyDataSetChanged()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(viewType, parent, false).run {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mItems!![position])
    }

    override fun getItemCount(): Int = mItems?.size ?: 0

    override fun getItemViewType(position: Int): Int = R.layout.item_menu_bestseller_dish

    interface SessionTrendingDishInteraction {
        fun onDishClick(itemModel: TrendingDishModel): Boolean

        fun onItemChanged(item: TrendingDishModel?, count: Int, position: Int): Boolean

    }

    fun notifyItemCount(pos: Int, orderedItem: OrderedItemModel){
//        updateTrendingOrderCount(orderedItem)
        notifyItemChanged(pos)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.im_menu_dish_group_icon)
        internal lateinit var imDishGroupIcon: ImageView
        @BindView(R.id.im_menu_dish_image)
        internal lateinit var imDishImage: ImageView
        @BindView(R.id.im_menu_dish_name)
        internal lateinit var tvName: TextView
        @BindView(R.id.tv_menu_dish_price)
        internal lateinit var tvPrice: TextView
        @BindView(R.id.btn_as_menu_bestseller_item_add)
        internal lateinit var btnItemAdd: TextView
        @BindView(R.id.container_menu_bestseller_item_quantity)
        internal lateinit var containerItemQuantity: ViewGroup
        @BindView(R.id.tv_menu_bestseller_item_quantity_number)
        internal lateinit var tvQuantityNumber: TextView
        @BindView(R.id.tv_menu_bestseller_item_quantity_decrement)
        internal lateinit var tvQuantityDecrement: TextView
        @BindView(R.id.tv_menu_bestseller_item_quantity_increment)
        internal lateinit var tvQuantityIncrement: TextView

        private lateinit var mItemModel: TrendingDishModel
        private var mCount = 1

        init {
            ButterKnife.bind(this, itemView)

            btnItemAdd.setOnClickListener(DebouncedOnClickListener {
                if (mListener != null) {
                    if (!mListener.onDishClick(mItemModel)) {
                        Utils.toast(itemView.context, "Not allowed!")
                        return@DebouncedOnClickListener
                    }
                }
                showQuantitySelection(1)
            })


            tvQuantityNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    val count = s.toString().toInt()
                    if (count < mCount && mItemModel.isComplexItem) {
                        disallowDecreaseCount()
                        mCount = Math.min(mCount, count + 1)
                        displayQuantity(mCount)
                        return
                    }
                    if (!mListener!!.onItemChanged(mItemModel, count, layoutPosition)) {
                        displayQuantity(0)
                        hideQuantitySelection()
                        return
                    }
                    if (count == 0) hideQuantitySelection()
                    mCount = count
                }
            })

            tvQuantityDecrement.setOnClickListener { decreaseQuantity() }
            tvQuantityIncrement.setOnClickListener(DebouncedOnClickListener { increaseQuantity() })
        }

        internal fun bindData(itemModel: TrendingDishModel) {
            this.mItemModel = itemModel

            if (itemModel.image.contains("static/menu/icons")) {
                Utils.loadImageOrDefault(imDishGroupIcon, itemModel.image, 0)
                imDishGroupIcon.setVisibility(View.VISIBLE)
                imDishImage.setVisibility(View.GONE)
            } else {
                Utils.loadImageOrDefault(imDishImage, itemModel.image, 0)
                imDishGroupIcon.setVisibility(View.GONE)
                imDishImage.setVisibility(View.VISIBLE)
            }
            tvName.text = itemModel.name
            tvPrice.text = Utils.formatCurrencyAmount(itemView.context, itemModel.typeCosts[0])

            changeQuantity(itemModel.count)

        }

        internal fun hideQuantitySelection() {
            containerItemQuantity.visibility = View.GONE
            btnItemAdd.visibility = View.VISIBLE
        }

        private fun showQuantitySelection(count: Int) {
            btnItemAdd.visibility = View.GONE
            containerItemQuantity.visibility = View.VISIBLE
            displayQuantity(count)
        }


        fun changeQuantity(count: Int) {
            mCount = count
            if (count <= 0) hideQuantitySelection()
            else showQuantitySelection(count)
        }

        private fun increaseQuantity() {
            mCount++
            displayQuantity(mCount)
        }

        private fun decreaseQuantity() {
            if (mItemModel.isComplexItem) {
                disallowDecreaseCount()
                return
            }
            if (mCount <= 0)
                return
            mCount--
            displayQuantity(mCount)
        }

        private fun displayQuantity(number: Int) {
            tvQuantityNumber.text = number.toString()
            mItemModel.count = number
        }

        private fun disallowDecreaseCount() {
            Utils.toast(itemView.context, "Not allowed to change item count from here - use cart.")
        }
    }


    internal inner class PostDiffCallback(private val oldPosts: List<TrendingDishModel>, private val newPosts: List<TrendingDishModel>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldPosts.size

        override fun getNewListSize(): Int = newPosts.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPosts[oldItemPosition].pk == newPosts[newItemPosition].pk
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPosts[oldItemPosition].equals(newPosts[newItemPosition])
        }
    }


}