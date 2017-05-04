package is.hellos.demos;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import is.hellos.demos.network.ApiService;
import retrofit2.Retrofit;

public class MainApplication extends Application {

    @Nullable
    private ApiService apiService = null;


    @NonNull
    public ApiService getApiService() {
        if (this.apiService == null) {
            this.apiService = new Retrofit.Builder()
                    .baseUrl("todo") //todo update
                    .build()
                    .create(ApiService.class);
        }
        return this.apiService;
    }

}
