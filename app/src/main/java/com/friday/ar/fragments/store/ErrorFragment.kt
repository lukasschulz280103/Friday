package com.friday.ar.fragments.store


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.friday.ar.R
import com.friday.ar.util.FireStoreCodeInterpreter
import com.google.firebase.firestore.FirebaseFirestoreException


/**
 * Error Fragment which shows an error page to the user.
 *
 * @see MainStoreFragment, com.friday.HeadDisplayClient.store.fragments.StoreFeaturedFragment
 */
class ErrorFragment(private val e: FirebaseFirestoreException)// Required empty public constructor
    : Fragment() {
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val errInterpreter = FireStoreCodeInterpreter(mContext!!, e)
        val v = inflater.inflate(R.layout.fragment_error, container, false)
        val retry = v.findViewById<Button>(R.id.retry)
        (v.findViewById<View>(R.id.errTitle) as TextView).text = getString(R.string.loading_err_title, errInterpreter.code.toString())
        (v.findViewById<View>(R.id.subtitle_err) as TextView).text = errInterpreter.message
        (v.findViewById<View>(R.id.fullMessage) as TextView).text = errInterpreter.exceptionMessage
        retry.setOnClickListener { view ->
            fragmentManager!!.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .add(R.id.store_frag_container, MainStoreFragment())
                    .remove(this)
                    .commit()
        }
        return v
    }

}
