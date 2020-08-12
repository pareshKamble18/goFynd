package com.paresh.gofynd.adapter

import android.content.Context
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.paresh.gofynd.R
import com.paresh.gofynd.model.Article
import com.paresh.gofynd.utils.MySpannable
import java.util.*


class ArticleAdapter(context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    private var isLoadingAdded = false
    private var articleList: MutableList<Article>?
    var article: Article? = null
    var context: Context

    // Paging :
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    fun getArticleList(): List<Article>? {
        return articleList
    }

    fun setArticleList(mArticleList: MutableList<Article>?) {
        articleList = mArticleList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var viewHolder: ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> viewHolder = getViewHolder(parent, inflater)
            LOADING -> {
                val v2: View =
                    inflater.inflate(R.layout.rv_item_loading, parent, false)
                viewHolder = LoadingViewHolder(v2)
            }
        }
        return viewHolder!!
    }

    private fun getViewHolder(parent: ViewGroup, inflater: LayoutInflater): ViewHolder {
        val viewHolder: ViewHolder
        val v1: View = inflater.inflate(R.layout.rv_layout_articles, parent, false)
        viewHolder = ArticleHolder(v1)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        article = articleList!![position] // Movie
        when (getItemViewType(position)) {
            ITEM -> {
                populateItemRows(viewHolder as ArticleHolder, position)
                Log.e("constants", "ArticleHolder")
            }
            LOADING -> {
                Log.e("constants", "LoadingViewHolder")
                showLoadingView(viewHolder as LoadingViewHolder, position)
            }
        }
        /*if (viewHolder instanceof ArticleHolder) {

            populateItemRows((ArticleHolder) viewHolder, position);

            Log.e("constants",  "ArticleHolder");

        } else if (viewHolder instanceof LoadingViewHolder) {
            Log.e("constants",  "LoadingViewHolder");
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }*/
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == articleList!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    override fun getItemCount(): Int {
        return if (articleList == null) 0 else articleList!!.size
    }

    inner class ArticleHolder(v: View) : ViewHolder(v) {
        var txt_author: TextView
        var txt_title: TextView
        var txt_publishedAt: TextView
        var txt_desc: TextView
        var imgV_url: ImageView

        init {
            txt_author = v.findViewById(R.id.txt_author)
            txt_title = v.findViewById(R.id.txt_title)
            txt_publishedAt = v.findViewById(R.id.txt_publishedAt)
            txt_desc = v.findViewById(R.id.txt_desc)
            imgV_url = v.findViewById(R.id.imgV_url)
        }
    }

    private inner class LoadingViewHolder(itemView: View) :
        ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    private fun showLoadingView(
        viewHolder: LoadingViewHolder,
        position: Int
    ) {
        //ProgressBar would be displayed
        viewHolder.progressBar.visibility = View.VISIBLE
    }

    private fun populateItemRows(holder: ArticleHolder, position: Int) {
        holder.txt_author.setText(articleList!![position].getAuthor())
        holder.txt_title.setText(articleList!![position].getTitle())
        holder.txt_publishedAt.setText(articleList!![position].getPublishedAt())
        holder.txt_desc.setText(articleList!![position].getDescription())
        makeTextViewResizable(holder.txt_desc, 2, "See More", true)
        Glide.with(context).load(articleList!![position].getUrlToImage()).into(holder.imgV_url)
    }

    // Helpers :
    fun add(r: Article) {
        articleList!!.add(r)
        notifyItemInserted(articleList!!.size - 1)
    }

    fun addAll(moveResults: List<Article>?) {

        for (result in moveResults!!) {
            add(result)
        }

    }

    fun updateTodoList(data: List<Article?>?) {
        articleList!!.clear()
        //        notifyDataSetChanged();
    }

    fun remove(r: Article?) {
        val position = articleList!!.indexOf(r)
        if (position > -1) {
            articleList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Article())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = articleList!!.size - 1
        val result: Article = getItem(position)
        if (result != null) {
            articleList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Article {
        return articleList!![position]
    }

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1

        // End Helpers
        // Read More :
        fun makeTextViewResizable(
            tv: TextView,
            maxLine: Int,
            expandText: String,
            viewMore: Boolean
        ) {
            if (tv.tag == null) {
                tv.tag = tv.text
            }
            val vto = tv.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val obs = tv.viewTreeObserver
                    obs.removeGlobalOnLayoutListener(this)
                    if (maxLine == 0) {
                        val lineEndIndex = tv.layout.getLineEnd(0)
                        val text =
                            tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                                .toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                        val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                        val text =
                            tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                                .toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    } else {
                        val lineEndIndex =
                            tv.layout.getLineEnd(tv.layout.lineCount - 1)
                        val text =
                            tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(tv.text.toString()),
                                tv,
                                lineEndIndex,
                                expandText,
                                viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    }
                }
            })
        }

        private fun addClickablePartTextViewResizable(
            strSpanned: Spanned, tv: TextView,
            maxLine: Int, spanableText: String, viewMore: Boolean
        ): SpannableStringBuilder {
            val str = strSpanned.toString()
            val ssb = SpannableStringBuilder(strSpanned)
            if (str.contains(spanableText)) {
                ssb.setSpan(object : MySpannable(false) {
                  override fun onClick(widget: View) {
                        if (viewMore) {
                            tv.layoutParams = tv.layoutParams
                            tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                            tv.invalidate()
                            makeTextViewResizable(
                                tv,
                                -1,
                                "See Less",
                                false
                            )
                        } else {
                            tv.layoutParams = tv.layoutParams
                            tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                            tv.invalidate()
                            makeTextViewResizable(
                                tv,
                                2,
                                ".. See More",
                                true
                            )
                        }
                    }
                }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
            }
            return ssb
        }
    }

    /*public ArticleAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }*/
    init {
        articleList = ArrayList<Article>()
        this.context = context
    }
}