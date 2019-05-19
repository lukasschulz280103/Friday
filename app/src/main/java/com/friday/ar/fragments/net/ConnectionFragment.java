package com.friday.ar.fragments.net;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.friday.ar.fragments.store.ErrorFragment;

/**
 * Extendable Fragment class to automatically implement {@link OnConnectionStateChangedListener}
 * This class is necessary to properly access unknown subclasses
 *
 * @see OnConnectionStateChangedListener
 * @see ErrorFragment
 */
public class ConnectionFragment extends Fragment implements OnConnectionStateChangedListener {

    @Override
    public void onConnected() {

    }

    @Override
    public void onError(Exception e) {
        Log.e("ConnectionFragment", e.getMessage());
    }
}

