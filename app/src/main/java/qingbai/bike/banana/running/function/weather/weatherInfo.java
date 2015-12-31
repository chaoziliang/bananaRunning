package qingbai.bike.banana.running.function.weather;

import java.util.ArrayList;

/**
 * Created by chaoziliang on 15/12/28.
 */
public class weatherInfo {
    public String mCity;
    public String mTemplature;
    public String mWind;
    public String mWding;
    public String mQuerty;
    public String mWeatherDes;
    public String mPmValue;

    public String mTime;

    public ArrayList mForecastDailyList;

    public weatherInfo() {
        mForecastDailyList = new ArrayList<ForecastDaily>();
    }

    public void addForecastDaily(String tmp, String wind, String wding, String weatherDes, String time) {

        mForecastDailyList.add(new ForecastDaily(tmp, wind, wding, weatherDes, time));
    }

    public void addForecastDaily(ForecastDaily daily) {
        mForecastDailyList.add(daily);
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (mForecastDailyList.size() > 0) {
            for (int i = 0; i < mForecastDailyList.size(); i++) {
                sb.append("forecast ");
                sb.append(mForecastDailyList.get(i).toString());
                sb.append("\n");
            }
        }

        sb.append("city: " + mCity + " tmplature: "
                + mTemplature + " Wding: "
                + mWding + " Wind: "
                + mWind + " weatherDes: " + mWeatherDes
                + " Query:" + mQuerty
                + " pm25: " + mPmValue
                + " time: " + mTime);

        return sb.toString();
    }

    public static class ForecastDaily {
        public String mTime;
        public String mTemplature;
        public String mWind;
        public String mWding;
        //        public String mQuerty;
        public String mWeatherDes;
//        public String mPmValue;

        ForecastDaily(String tmp, String wind, String wding, String weatherDes, String time) {
            mTemplature = tmp;
            mWding = wding;
            mWind = wind;
            mWeatherDes = weatherDes;
            mTime = time;
        }

        public ForecastDaily() {

        }

        @Override
        public String toString() {

            return "templature: " + mTemplature + " Wding: "
                    + mWding + " Wind: "
                    + mWind + " weatherDes: " + mWeatherDes
                    + " time: " + mTime;

        }
    }
}
