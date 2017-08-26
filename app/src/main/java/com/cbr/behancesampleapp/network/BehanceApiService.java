package com.cbr.behancesampleapp.network;

import com.cbr.behancesampleapp.model.BehanceUser;
import com.cbr.behancesampleapp.model.BehanceUserResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by dimitrios on 24/08/2017.
 */

public interface BehanceApiService {

    @GET(Urls.Behance.Users.GET_USERS)
    Observable<BehanceUserResponse> getUsers(@QueryMap Map<String, Object> queryParams);

    @GET(Urls.Behance.Users.GET_USER_BY_ID)
    Observable<Response<BehanceUser>> getUserById(@QueryMap Map<String, Object> queryParams);

    @GET(Urls.Behance.Users.GET_USER_BY_ID)
    Observable<Response<BehanceUser>> getUserByUsername(@QueryMap Map<String, Object> queryParams);

}
