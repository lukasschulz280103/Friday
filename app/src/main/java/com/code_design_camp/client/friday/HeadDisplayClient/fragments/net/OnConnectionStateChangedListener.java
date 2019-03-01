package com.code_design_camp.client.friday.HeadDisplayClient.fragments.net;

public interface OnConnectionStateChangedListener {
    void onConnected();

    void onError(Exception e);
}
