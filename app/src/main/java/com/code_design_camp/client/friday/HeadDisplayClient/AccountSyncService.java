package com.code_design_camp.client.friday.HeadDisplayClient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AccountSyncService extends Service {
    public AccountSyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
