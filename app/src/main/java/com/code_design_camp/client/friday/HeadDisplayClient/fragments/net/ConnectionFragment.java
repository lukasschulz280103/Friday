package com.code_design_camp.client.friday.HeadDisplayClient.fragments.net;

import android.util.Log;

import androidx.fragment.app.Fragment;

/**
 * Extendable Fragment class to automatically implement {@link OnConnectionStateChangedListener}
 * This class is necessary to properly access unknown subclasses
 * @see OnConnectionStateChangedListener
 * @see com.code_design_camp.client.friday.HeadDisplayClient.fragments.store.ErrorFragment
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

