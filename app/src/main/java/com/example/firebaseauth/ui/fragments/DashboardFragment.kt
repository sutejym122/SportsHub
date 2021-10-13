package com.example.firebaseauth.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauth.R
import com.example.firebaseauth.databinding.FragmentDashboardBinding
import com.example.firebaseauth.firestore.FirestoreClass
import com.example.firebaseauth.models.Product
import com.example.firebaseauth.ui.activities.CartListActivity
import com.example.firebaseauth.ui.activities.ProductDetailsActivity
import com.example.firebaseauth.ui.activities.SettingsActivity
import com.example.firebaseauth.ui.adapters.DashboardItemsListAdapter
import com.example.firebaseauth.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : BaseFragment() {
    /*private lateinit var dashboardViewModel: DashboardViewModel*/
    private lateinit var searchlist:ArrayList<Product>
    private lateinit var rvlist:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /*dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)*/

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)


        /*dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        val searchbar=root.findViewById<EditText>(R.id.searchbar)
        rvlist=root.findViewById<RecyclerView>(R.id.rv_dashboard_items)
        rvlist.setHasFixedSize(true)
        rvlist.layoutManager=GridLayoutManager(context,2)
        searchlist= arrayListOf()
        searchbar.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rvlist.visibility=View.VISIBLE
                searchlist.clear()
                val text=s.toString()
                getSearchData(text)
                
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        return root
    }

    private fun getSearchData(s: String) {
        if(s!=null && s!=""){
            val dbref=FirebaseFirestore.getInstance().collection(Constants.PRODUCTS).orderBy("title").startAt(s).endAt(s + "\uf8ff")
            dbref.get().addOnSuccessListener {
                for(item in it.documents){
                    val product=item.toObject(Product::class.java)
                    product!!.product_id=item.id
                    searchlist.add(product!!)
                }
                rvlist.adapter=DashboardItemsListAdapter(requireContext(),searchlist)
            }
        }
        else if(s==""){
            rvlist.visibility=View.GONE
        }
    }
    // END

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_settings -> {


                startActivity(Intent(activity, SettingsActivity::class.java))

                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity,CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {

        // Hide the progress dialog.
        hideProgressDialog()

        if (dashboardItemsList.size > 0) {

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            rv_dashboard_items.adapter = adapter
            /*
            adapter.setOnClickListener(object: DashboardItemsListAdapter.OnClickListener{
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(context,ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,product.user_id)
                    startActivity(intent)
                }
            })

             */
        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

}