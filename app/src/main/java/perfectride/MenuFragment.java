package perfectride;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import static android.content.Context.LOCATION_SERVICE;

public class MenuFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    private View rootView;
    private EditText postalEt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        postalEt = (EditText) rootView.findViewById(R.id.postal_et);
        postalEt.addTextChangedListener(new PostalTextWatcher());
        rootView.findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                RideDetailsFragment fragment = new RideDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("postalCode", postalEt.getText().toString());
                bundle.putString("location", currentLocation);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_layout, fragment, fragment.getClass().getSimpleName());
                fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
                fragmentTransaction.commit();
            }
        });

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            initGps();
        } else {
            showGPSDisabledAlertToUser();
        }


        return rootView;
    }

    private void initGps() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        LocationHelper locationHelper = new LocationHelper(getContext(), new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location == null)
                    return;

                currentLocation = location.getLatitude() + "," + location.getLongitude();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

        locationHelper.startRetrievingLocation();
    }

    private String currentLocation = "";


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    class PostalTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String name = editable.toString();
            if (name.length() == 6) {
                boolean charCheck = true;
                boolean digitCheck = true;
                char[] chars = new char[6];
                name.getChars(0, name.length() - 1, chars, 0);
                for (int i = 0; i < name.length(); i++) {
                    if (i % 2 == 0) {
                        if (Character.isDigit(chars[i])) {
                            charCheck = false;
                        }
                    } else {
                        if (Character.isLetter(chars[i])) {
                            digitCheck = false;
                        }
                    }
                }
                if (charCheck && digitCheck) {
                    rootView.findViewById(R.id.search_btn).setVisibility(View.VISIBLE);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("PerfectRide")
                            .setMessage("Not a valid postal code.")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }

            } else {
                rootView.findViewById(R.id.search_btn).setVisibility(View.INVISIBLE);
            }
        }
    }
}
