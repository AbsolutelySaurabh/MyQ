package dd.com.myq.Activity;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
     private  static final String REG_TOKEN="REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        String recent_token= FirebaseInstanceId.getInstance().getToken();

        Log.d(REG_TOKEN,recent_token);
    }
}
