package com.bellatrix.aditi.documentorganizer;

import com.bellatrix.aditi.documentorganizer.Database.DBQueries;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements FolderAdapter.onListItemClickLister{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;


    private RecyclerView recyclerView;
    private FolderAdapter folderAdapter;
    FloatingActionsMenu addMenu;
    FloatingActionButton camera_button, gallery_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.rv_folders);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);
        folderAdapter = new FolderAdapter(this);
        recyclerView.setAdapter(folderAdapter);

        addMenu = (FloatingActionsMenu) findViewById(R.id.add_actions);
        camera_button = (FloatingActionButton) findViewById(R.id.camera_action);
        gallery_button = (FloatingActionButton) findViewById(R.id.gallery_action);

        camera_button.setIcon(R.drawable.ic_camera);
        gallery_button.setIcon(R.drawable.ic_gallery);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
        gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGalleryChooser();
            }
        });
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uriToBitmap(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            upload((Bitmap)data.getExtras().get("data"));
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void upload(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        DBQueries.insertDocument(this,byteArray);
    }

    public void uriToBitmap(Uri uri) {
        if (uri != null) {
            try {
                Bitmap photo = (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                upload(photo);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Image picking failed", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "Image picker gave us a null image.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                super.onSearchRequested();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSearchRequested() {
//        pauseSomeStuff();
//        can insert additional data here
//        Bundle appData = new Bundle();
//        appData.putBoolean(SearchableActivity.JARGON, true);
//        startSearch(null, false, appData, false);
//        return true;
        return super.onSearchRequested();
    }

    @Override
    public void onListItemClick(int index) {
        Folder folder;
        if ((folder = Constants.FOLDERS.get(index))==null)
            return; // bail if returned null*/

        // TODO: Add intent
        Toast.makeText(this,folder.folderName,Toast.LENGTH_SHORT).show();
    }

    // TODO: Provision to add a new folder

    // TODO: SwapList if a new folder is added
}
