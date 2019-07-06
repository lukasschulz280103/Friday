package com.friday.ar.preference

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

import com.friday.ar.R
import com.google.firebase.auth.FirebaseAuth

class AccountPreference : Preference {
    internal var auth = FirebaseAuth.getInstance()
    private var firebaseUser = auth.currentUser

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        val accountName: TextView = holder.findViewById(R.id.account_pref_name) as TextView
        val accountEmail: TextView = holder.findViewById(R.id.account_preference_email) as TextView
        val accountProvider: ImageView = holder.findViewById(R.id.account_preference_provider) as ImageView
        val accountImage: ImageView = holder.findViewById(R.id.account_img) as ImageView
        val notSignedInTextView: TextView = holder.findViewById(R.id.account_preference_text_not_signed_in) as TextView
        val content: ConstraintLayout = holder.findViewById(R.id.account_preference_content) as ConstraintLayout
        if (firebaseUser != null) {
            notSignedInTextView.visibility = View.GONE
            accountName.text = firebaseUser!!.displayName
            accountEmail.text = firebaseUser!!.email
            accountImage.setImageURI(Uri.parse(context.filesDir.toString() + "/profile/avatar.jpg"))
            for (user in FirebaseAuth.getInstance().currentUser!!.providerData) {
                when (user.providerId) {
                    "google.com" -> {
                        accountProvider.setImageResource(R.drawable.googleg_standard_color_18)
                    }
                }
            }
        } else {
            content.visibility = View.GONE
        }
    }
}
