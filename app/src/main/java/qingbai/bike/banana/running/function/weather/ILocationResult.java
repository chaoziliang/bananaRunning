package qingbai.bike.banana.running.function.weather;

/**
 * Created by chaoziliang on 15/12/29.
 */
public interface ILocationResult {


     void onSuccessfulLocation(String city);

     void onFailLocation(String errorMessage);

}
