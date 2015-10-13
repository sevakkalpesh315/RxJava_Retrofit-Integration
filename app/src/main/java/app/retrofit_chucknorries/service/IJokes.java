package app.retrofit_chucknorries.service;

import app.retrofit_chucknorries.model.JokesModel;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by kalpesh on 10/10/2015.
 */

/**
 * Retrofit interface to  API methods
 */
public interface IJokes {


  //  @GET("/jokenumber")
 //   public void getJokes(Callback <JokesModel> response);
  @GET("/jokenumber")
    Observable<JokesModel> getJokes();
}

