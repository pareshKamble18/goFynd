package com.paresh.gofynd.view


import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paresh.gofynd.R
import com.paresh.gofynd.adapter.ArticleAdapter
import com.paresh.gofynd.model.Article
import com.paresh.gofynd.model.MainModel
import com.paresh.gofynd.utils.Constants
import com.paresh.gofynd.utils.PaginationScrollListener
import com.paresh.gofynd.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    var mainActivityViewModel: MainActivityViewModel? = null
    var constants: Constants? = null

    // RV :
    private var recyclerView: RecyclerView? = null
    var main_progress: ProgressBar? = null
   lateinit var linearLayoutManager: LinearLayoutManager
    var recyclerViewAdapter: ArticleAdapter? = null

    // Another Page :
    private val PAGE_START = 1
    private var isLoading = false
    private var isLastPage = false
    open val totalPageCount = 10
        get() {
            return field
        }
    private var currentPage = PAGE_START
    private val TOTAL_PAGES = 10



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        constants = Constants(this)

        recyclerViewAdapter = ArticleAdapter(this)

        //setting up recyclerview

        //setting up recyclerview
        recyclerView!!.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager


        if (constants!!.isOnline) {
            initScrollListener()
            mainActivityViewModel!!.data
                .observe(this, object : Observer<MainModel> {
                    override fun onChanged(response: MainModel) {
                        val mainModel: MainModel = response
                        val articleList: List<Article>? = mainModel.articles
                        recyclerView!!.visibility = View.VISIBLE
                        main_progress!!.visibility = View.GONE
                        recyclerViewAdapter!!.addAll(articleList)
                        recyclerView!!.adapter = recyclerViewAdapter

                        // DB :
                        mainActivityViewModel!!.initDeleteDbData()
                        mainActivityViewModel!!.insert(articleList!!)
                        if (currentPage <= totalPageCount) recyclerViewAdapter!!.addLoadingFooter() else isLastPage =
                            true
                    }
                })
        } else {
            mainActivityViewModel!!.initGetDbData()
            mainActivityViewModel!!.dbData!!.observe(
                this,
                object : Observer<List<Article>> {
                    override fun onChanged(articleList: List<Article>) {
                        recyclerView!!.visibility = View.VISIBLE
                        main_progress!!.visibility = View.GONE
                        recyclerViewAdapter!!.addAll(articleList)
                        recyclerView!!.adapter = recyclerViewAdapter
                    }


                })
        }
    }

    private fun initialize() {
        main_progress = findViewById(R.id.main_progress)
        recyclerView = findViewById(R.id.rv_articles)
    }

    private fun initScrollListener() {

        recyclerView!!.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                // mocking network delay for API call
                Handler().postDelayed({
                    mainActivityViewModel!!.initLoadMore(currentPage)
                    mainActivityViewModel!!.loadMoreData!!.observe(
                        this@MainActivity,
                        Observer<MainModel> { response ->
                            val articleList: List<Article>? =
                                response.articles
                            val status: String? = response.status
                            if (status == "ok") {
                                recyclerViewAdapter!!.removeLoadingFooter()
                                isLoading = false
                                recyclerViewAdapter!!.addAll(articleList)
                                if (currentPage != TOTAL_PAGES) recyclerViewAdapter!!.addLoadingFooter() else isLastPage =
                                    true

                                // DB :
                                mainActivityViewModel!!.insert(articleList!!)
                            }
                        })
                }, 1000)
            }

            override val totalPageCount: Int
                get() = TOTAL_PAGES

            override var isLastPage1: Boolean = isLastPage

            override var isLoading1: Boolean  = isLoading
        })
    }

}