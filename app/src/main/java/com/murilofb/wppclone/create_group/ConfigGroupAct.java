package com.murilofb.wppclone.create_group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.ContactsAdapter;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.ToastH;
import com.murilofb.wppclone.home.HomeActivity;
import com.murilofb.wppclone.models.GroupModel;
import com.murilofb.wppclone.models.UserModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigGroupAct extends AppCompatActivity {
    private static final int REQUEST_OPEN_GALLERY = 1;
    private TextView txtParticipantsCount;
    private RecyclerView recyclerParticipants;
    private FloatingActionButton fabFinishCreation;
    private CircleImageView groupIcon;
    private EditText edtGroupName;

    private List<UserModel> groupList;
    private Bitmap groupIconBitmap;
    private ToastH toastH;
    private GroupModel groupModel;
    private FirebaseH.RealtimeDatabase database;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabFinishCreation:
                    String groupName = edtGroupName.getText().toString();
                    if (!groupName.equals("")) {
                        groupList.add(UserModel.getCurrentUser());
                        groupModel.setParticipants(groupList);
                        groupModel.setGroupName(groupName);
                        database.createGroup(groupModel, groupIconBitmap);
                        startActivity(new Intent(ConfigGroupAct.this, HomeActivity.class));
                        finishAffinity();
                    } else {
                        toastH.showToast(getString(R.string.toast_empty_group_name));
                    }

                    break;
                case R.id.groupIcon:
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    try {
                        startActivityForResult(i, REQUEST_OPEN_GALLERY);
                    } catch (Exception e) {
                        toastH.showToast(getString(R.string.error_pick_from_gallery));
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_group_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.toolbar_create_group_title));
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri imageUri;
            switch (requestCode) {
                case REQUEST_OPEN_GALLERY:
                    imageUri = data.getData();
                    CropImage.activity(imageUri)
                            .setAspectRatio(1, 1)
                            .start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    imageUri = result.getUri();
                    try {
                        groupIconBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.default_image)
                            .into(groupIcon);
                    break;
            }
        }
    }

    private void initView() {
        toastH = new ToastH(this);

        fabFinishCreation = findViewById(R.id.fabFinishCreation);
        fabFinishCreation.setOnClickListener(clickListener);

        groupIcon = findViewById(R.id.groupIcon);
        groupIcon.setOnClickListener(clickListener);

        groupList = (List<UserModel>) getIntent().getSerializableExtra("groupList");

        txtParticipantsCount = findViewById(R.id.txtParticipantsCount);
        txtParticipantsCount.setText(getString(R.string.txt_participants_count) + " " + groupList.size());

        edtGroupName = findViewById(R.id.edtGroupName);

        database = new FirebaseH().new RealtimeDatabase(this);

        ContactsAdapter adapter = new ContactsAdapter(groupList, null);
        recyclerParticipants = findViewById(R.id.recyclerParticipants);
        recyclerParticipants.setLayoutManager(new GridLayoutManager(this, 5, RecyclerView.VERTICAL, false));
        recyclerParticipants.setAdapter(adapter);

        groupModel = new GroupModel();
    }

}
