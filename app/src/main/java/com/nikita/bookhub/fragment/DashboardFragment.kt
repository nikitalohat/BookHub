package com.nikita.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nikita.bookhub.R
import com.nikita.bookhub.adapter.DashboardRecyclerAdapter
import com.nikita.bookhub.model.Book
import com.nikita.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    //lateinit var btnCheckInternet : Button
    lateinit var recyclerAdapter : DashboardRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var  progressBar: ProgressBar


    val bookInfoList = arrayListOf<Book>()

    var ratingComparator = Comparator<Book>{book1, book2 ->

      if(book1.bookRating.compareTo(book2.bookRating, true) == 0){
          //sort according to name if rating is same
          book1.bookName.compareTo(book2.bookName, true)
      }else{
          book1.bookRating.compareTo(book2.bookRating, true)
      }

    }

   /* val bookInfoList = arrayListOf<Book>(
        Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
        Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
        Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
        Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
        Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
        Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
        Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
        Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
        Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
        Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
    )*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)

        //btnCheckInternet = view.findViewById(R.id.btnCheckInternet)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        setHasOptionsMenu(true)  //to tell compiler that there is options on toolbar
       /* btnCheckInternet.setOnClickListener{
            if(ConnectionManager().checkConnectivity(activity as Context)){
                //Internet is available
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("OK"){text, listener ->
                    //Do nothing
                }
                dialog.setNegativeButton("Cancel"){text, listener ->
                    //Do nothing
                }
                dialog.create()
                dialog.show()
            }
            else{
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("OK"){text, listener ->
                    //Do nothing
                }
                dialog.setNegativeButton("Cancel"){text, listener ->
                    //Do nothing
                }
                dialog.create()
                dialog.show()
                //Internet is not available
            }
        }*/





        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    // Handle Response
                    if(activity != null) {
                        try {
                            progressLayout.visibility = View.GONE
                            val success = it.getBoolean("success")
                            if (success) {
                                val data = it.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val bookJsonObject = data.getJSONObject(i)
                                    val bookObject = Book(
                                        bookJsonObject.getString("book_id"),
                                        bookJsonObject.getString("name"),
                                        bookJsonObject.getString("author"),
                                        bookJsonObject.getString("rating"),
                                        bookJsonObject.getString("price"),
                                        bookJsonObject.getString("image")
                                    )
                                    bookInfoList.add(bookObject)
                                    recyclerAdapter =
                                        DashboardRecyclerAdapter(activity as Context, bookInfoList)
                                    recyclerDashboard.adapter = recyclerAdapter
                                    recyclerDashboard.layoutManager = layoutManager
                                    /*.addItemDecoration(       //To Add Divider Line
                                DividerItemDecoration(
                                    recyclerDashboard.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )
                            )*/
                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Some Error Occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                activity as Context,
                                "Some Unexpected Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }, Response.ErrorListener {
                    //Handle Error
                    if(activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred! ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "7ad21b63c63245"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        }

        else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}