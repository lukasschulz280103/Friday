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
import com.friday.ar.account.data.UserStore
import com.google.firebase.auth.FirebaseAuth

class AccountPreference(context: Context, attributeSet: AttributeSet) : Preference(context, attributeSet) {
    private var auth = FirebaseAuth.getInstance()
    private var firebaseUser = auth.currentUser

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        val accountName: TextView = holder.findViewById(R.id.account_pref_name) as TextView
        val accountEmail: TextView = holder.findViewById(R.id.account_preference_email) as TextView
        val accountProviderImage: ImageView = holder.findViewById(R.id.account_preference_provider) as ImageView
        val accountImage: ImageView = holder.findViewById(R.id.account_img) as ImageView
        val notSignedInTextView: TextView = holder.findViewById(R.id.account_preference_text_not_signed_in) as TextView
        val content: ConstraintLayout = holder.findViewById(R.id.account_preference_content) as ConstraintLayout
        if (firebaseUser != null) {
            notSignedInTextView.visibility = View.GONE
            val user = UserStore(context)
            if (user.avatarFile.exists()) accountImage.setImageURI(Uri.parse(user.avatarFile.path))
            else accountImage.setImageResource(R.drawable.ic_twotone_account_circle_24px)
            accountName.text = firebaseUser!!.displayName
            accountEmail.text = firebaseUser!!.email
            for (userInfo in FirebaseAuth.getInstance().currentUser!!.providerData) {
                when (userInfo.providerId) {
                    "google.com" -> {
                        accountProviderImage.setImageResource(R.drawable.googleg_standard_color_18)
                    }
                    else -> {
                        accountProviderImage.visibility = View.GONE
                    }
                }
            }
        } else {
            content.visibility = View.GONE
        }
    }
}
