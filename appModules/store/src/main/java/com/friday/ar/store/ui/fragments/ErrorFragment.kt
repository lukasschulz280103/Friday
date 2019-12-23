package com.friday.ar.store.ui.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.friday.ar.core.util.firebase.FireStoreCodeInterpreter
import com.friday.ar.store.R
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.fragment_error.*


/**
 * Error Fragment which shows an error page to the user.
 *
 * @see MainStoreFragment, com.friday.HeadDisplayClient.store.fragments.StoreFeaturedFragment
 */
class ErrorFragment(private val e: FirebaseFirestoreException) : Fragment() {
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val errInterpreter = FireStoreCodeInterpreter(mContext, e)
        super.onViewCreated(view, savedInstanceState)
        errTitle.text = getString(R.string.loading_err_title, errInterpreter.code.toString())
        subtitle_err.text = errInterpreter.message
        fullMessage.text = errInterpreter.exceptionMessage
    }

}
