package com.example.projetmobile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.projetmobile.entities.Habitat;
import com.example.projetmobile.entities.HabitatAdapter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

public class HabitatFragment extends Fragment {

    private ProgressDialog pDialog;
    private ListView listView;
    private List<Habitat> habitats;
    private HabitatAdapter adapter;

    public HabitatFragment() {
        habitats = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Associer le fragment à son layout
        View view = inflater.inflate(R.layout.fragment_habitat, container, false);

        // Récupérer la ListView
        listView = view.findViewById(R.id.listview);

        adapter = new HabitatAdapter(requireContext(), habitats);

        listView.setAdapter(adapter);

        getRemoteHabitatsWithAllData();

        return view;
    }

    public void getRemoteHabitatsWithAllData() {
        String urlString = "http://10.0.2.2/powerhome/getHabitatsWithUsersAndAppliances.php";

        pDialog = new ProgressDialog(this.requireActivity());
        pDialog.setMessage(getString(R.string.msglist));
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
                        pDialog.dismiss();
                        if(e == null && response != null) {
                            Log.d(this.getClass().getName(), "Result: " + response.getResult());
                            habitats.clear();
                            habitats.addAll(Habitat.getListFromJson(response.getResult()));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
