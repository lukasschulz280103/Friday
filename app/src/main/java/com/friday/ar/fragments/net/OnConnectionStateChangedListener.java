package com.friday.ar.fragments.net;

public interface OnConnectionStateChangedListener {
    void onConnected();

    void onError(Exception e);
}
