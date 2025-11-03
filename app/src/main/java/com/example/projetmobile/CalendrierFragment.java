package com.example.projetmobile;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.projetmobile.entities.Booking;
import com.example.projetmobile.entities.TimeSlot;
import com.example.projetmobile.entities.TimeSlotAdapter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;


import java.util.ArrayList;
import java.util.List;


public class CalendrierFragment extends Fragment {
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;

    private MaterialCalendarView calendarView;
    private List<TimeSlot> slots;
    private List<Booking> bookings;
    private ProgressDialog pDialog;
    private TimeSlotAdapter adapter;
    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendrier, container, false);

        calendarView = view.findViewById(R.id.calendrier);

        listView = view.findViewById(R.id.time_slot_list);
        slots = new ArrayList<>();
        bookings = new ArrayList<>();

        adapter = new TimeSlotAdapter(requireContext(), R.layout.item_time_slot, slots, bookings, this);
        listView.setAdapter(adapter);

        calendarView.setOnDateChangedListener(((widget, date, selected) -> {
            selectedMonth = date.getMonth();
            selectedYear = date.getYear();
            selectedDay = date.getDay();
            getAllReservedSlots(selectedMonth, selectedYear, selectedDay);
            getAllBookings(selectedMonth, selectedYear, selectedDay);
        }));

        return view;
    }


    public void getAllReservedSlots(int month, int year, int day){
        String urlString = "http://10.0.2.2/powerhome/getDailyTimeSlots.php?month=" + month + "&year=" + year + "&day=" + day;

        dismissProgressDialog();
        pDialog = new ProgressDialog(this.requireActivity());
        pDialog.setMessage(getString(R.string.msg_creneau));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        Ion.with(this.requireActivity())
                .load(urlString)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        dismissProgressDialog();
                        if(e == null && response != null) {
                            Log.d(this.getClass().getName(), "Result: " + response.getResult());
                            slots.clear();
                            slots.addAll(TimeSlot.getListFromJson(response.getResult()));
                            adapter.notifyDataSetChanged();
                            adapter.updateData(slots, bookings);
                        }
                    }
                });
    }

    public void getAllBookings(int month, int year, int day){
        String urlString = "http://10.0.2.2/powerhome/getDailyBookings.php?month=" + month + "&year=" + year + "&day=" + day;

        dismissProgressDialog();
        pDialog = new ProgressDialog(this.requireActivity());
        pDialog.setMessage(getString(R.string.msg_reservation));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        Ion.with(this.requireActivity())
                .load(urlString)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        dismissProgressDialog();
                        if(e == null && response != null) {
                            Log.d(this.getClass().getName(), "Result: " + response.getResult());
                            bookings.clear();
                            bookings.addAll(Booking.getListFromJson(response.getResult()));
                            adapter.updateData(slots, bookings);
                        }
                    }
                });
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void refreshTimeSlots() {
        getAllReservedSlots(selectedMonth, selectedYear, selectedDay);
        getAllBookings(selectedMonth, selectedYear, selectedDay);
    }

}
