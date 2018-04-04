package perfectride;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RideDetailsFragment extends Fragment {

    private View rootView;
    private EditText destinationET;
    private EditText distanceEt;
    private EditText estimateEt;
    private String location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ride_details, container, false);
        destinationET = rootView.findViewById(R.id.destination_et);
        distanceEt = rootView.findViewById(R.id.distance_et);
        estimateEt = rootView.findViewById(R.id.estimate_et);
        Button map_button = rootView.findViewById(R.id.map_button);
        Button confirm_button = rootView.findViewById(R.id.confirm_button);
        location = getArguments().getString("location", "");
        if (location == null || location.isEmpty()) {
            location = "43.773257,-79.335899";
        }
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityMap.class);
                intent.putExtra("startLocation", location);
                intent.putExtra("endLocation", getArguments().getString("postalCode"));
                startActivity(intent);
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
        destinationET.setText("");
        distanceEt.setText(" km");
        estimateEt.setText("");

        new getDistanceFromAPI().execute();

        return rootView;

    }

    private class getDistanceFromAPI extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            try {
                URL url = new URL("http://maps.googleapis.com/maps/api/distancematrix/json?origins=" + location + "&destinations=" + getArguments().getString("postalCode") + "&mode=driving&language=en-EN&sensor=false");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {
                Log.e("Fragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Fragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.optString("status").equalsIgnoreCase("OK")) {
                    String destination_addresses = object.optString("destination_addresses");
                    String substring = destination_addresses.substring(2, destination_addresses.length() - 2);
                    if (substring == null || substring.isEmpty()) {
                        Toast.makeText(getActivity(), "There is no location for this postal code", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        destinationET.setText(substring);
                        JSONObject rows = (JSONObject) object.getJSONArray("rows").get(0);
                        JSONObject elements = (JSONObject) rows.getJSONArray("elements").get(0);
                        JSONObject distance = elements.getJSONObject("distance");
                        int text = distance.optInt("value");
                        double ceil = Math.ceil((double) text / 1000);
                        distanceEt.setText(ceil + " KM");
                        estimateEt.setText(ceil + "");
                    }

                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("json", s);
        }
    }
}
