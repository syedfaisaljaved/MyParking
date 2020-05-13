package com.faisaljaved.myparking.Activities.ChatFragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faisaljaved.myparking.Activities.MessageActivity;
import com.faisaljaved.myparking.Activities.PostAdActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.adapters.ChatUsersListAdapter;
import com.faisaljaved.myparking.listener.OnDataClickListener;
import com.faisaljaved.myparking.models.ChatUsers;
import com.faisaljaved.myparking.viewmodels.ChatUsersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellingFragment extends Fragment implements OnDataClickListener {

    private static final String TAG = "SellingFragment";

    //Ui components
    private RecyclerView recyclerView;
    private ImageView mNoMsg;
    private Button mStartSelling;

    private ChatUsersListAdapter chatUsersListAdapter;
    private ChatUsersViewModel chatUsersViewModel;
    //firebase
    private FirebaseUser mUser;
    private String mUserId;

    public SellingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //firebase
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = mUser.getUid();

        //viewmodel initialize
        chatUsersViewModel = new ViewModelProvider(this).get(ChatUsersViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selling, container, false);

        recyclerView = view.findViewById(R.id.selling_recycler_view);
        mNoMsg = view.findViewById(R.id.no_messages_yet);
        mStartSelling = view.findViewById(R.id.start_selling_tv);
        mStartSelling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostAdActivity.class);
                getActivity().startActivity(intent);
            }
        });

        initRectyclerview();
        subscribeObservers();
        chatUsersViewModel.loadSellingUsersFromFirebase(mUserId);

        return view;
    }

    private void subscribeObservers() {
        chatUsersViewModel.getSellersList().observe(getViewLifecycleOwner(), new Observer<List<ChatUsers>>() {
            @Override
            public void onChanged(List<ChatUsers> chatUsers) {
                if (chatUsers!= null){
                    if (chatUsers.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        mNoMsg.setVisibility(View.VISIBLE);
                        mStartSelling.setVisibility(View.VISIBLE);
                    }else {
                        chatUsersListAdapter.setSellingUserData(chatUsers);
                        recyclerView.setVisibility(View.VISIBLE);
                        mNoMsg.setVisibility(View.GONE);
                        mStartSelling.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initRectyclerview() {
        chatUsersListAdapter = new ChatUsersListAdapter(this);
        recyclerView.setAdapter(chatUsersListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("userData",chatUsersListAdapter.getSelectedSUserData(position));
        intent.putExtra("sellerFragment", "sent");
        startActivity(intent);
    }
}
