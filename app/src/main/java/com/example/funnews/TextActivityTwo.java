package com.example.funnews;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextActivityTwo extends Activity implements View.OnClickListener{

    private ImageView img;
    private Button popBtn;
    private PopupWindow popupWindow;
    private Button pop_img;
    private Button pop_file;
    private Button pop_cancle;
    private View bt_zx;
    private ImageView homepage;
    private CustomeOnClickListener listener;
    private ImageView likepage;
    SharedPreferences sprfMain;
    SharedPreferences.Editor editorMain;
    private TextView userNa1;

    //相册请求码
    private static final int ALBUM_REQUEST_CODE = 1;
    //相机请求码
    private static final int CAMERA_REQUEST_CODE = 2;
    //剪裁请求码
    private static final int CROP_SMALL_PICTURE = 3;

    //调用照相机返回图片文件
    private File tempFile;
    //最后显示的图片文件
    private  String mFile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usr_bio);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        homepage=findViewById(R.id.homeBtn);
        likepage=findViewById(R.id.likeBtn);
        setStatusBar();
        registListener();
        initPopWindow();
        initView();
        load();

        //加载用户名
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String nameUser1 = "user";
        userNa1 = findViewById(R.id.userNa);
        userNa1.setText(nameUser1);

        bt_zx =findViewById(R.id.zhuxiao);
        bt_zx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSprfMain();
                Intent intent = new Intent(TextActivityTwo.this, MainActivity.class);
                startActivity(intent);
                TextActivityTwo.this.finish();
                Toast.makeText(TextActivityTwo.this,  "注销成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void resetSprfMain(){
        sprfMain= PreferenceManager.getDefaultSharedPreferences(this);
        editorMain=sprfMain.edit();
        editorMain.putBoolean("main",false);
        editorMain.commit();
    }

    private void registListener(){
        listener = new CustomeOnClickListener();
        homepage.setOnClickListener(listener);
        likepage.setOnClickListener(listener);
    }
    class CustomeOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.homeBtn:
                    Intent intent2 = new Intent(
                            TextActivityTwo.this,
                            MainActivity.class
                    );
                    startActivity(intent2);
                    break;
                case R.id.likeBtn:
                    Intent intent3 = new Intent(
                            TextActivityTwo.this,
                            sss.class
                    );
                    startActivity(intent3);
                    break;
            }
        }
    }




    private void initView() {
        img = findViewById(R.id.image);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一个参数是要将PopupWindow放到的View，第二个参数是位置，第三第四是偏移值
                popupWindow.showAtLocation(img, Gravity.BOTTOM, 0, 0);
            }
        });
    }

    public void initPopWindow() {
        View popView = getLayoutInflater().inflate(R.layout.pop_btn, null);

        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        pop_img = (Button) popView.findViewById(R.id.pop_img);
        pop_file = (Button) popView.findViewById(R.id.pop_file);
        pop_cancle = (Button) popView.findViewById(R.id.pop_cancle);
        pop_img.setOnClickListener(this);
        pop_file.setOnClickListener(this);
        pop_cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_img:
                //关闭popupWindow
                popupWindow.dismiss();
                getPicFromCamera();

                break;
            case R.id.pop_file:
                popupWindow.dismiss();
                getPicFromAlbm();
                break;
            case R.id.pop_cancle:
                popupWindow.dismiss();
                break;
        }
    }
    protected void setStatusBar() {//状态栏沉浸，状态栏颜色，状态栏系统图标的深浅色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (false) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorTheme));//设置状态栏背景色
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {
            //Toast.makeText(this, "低于4.4的android系统版本不存在沉浸式状态栏", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && true) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    private void save(String mFile){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();//获得SHaredPreferences.Editor对象
        editor.putBoolean("imageChange",true);//添加一个名为imageChange的boolean值，数值为true
        editor.putString("mFile",mFile);//保存imagePath图片路径
        editor.apply();//提交
    }

    private void load(){
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);//获得SharedPreferences的对象
        //括号里的判断是去找imageChange这个对应的数值，若是找不到，则是返回false，找到了的话就是我们上面定义的true，就会执行其中的语句
        if(preferences.getBoolean("imageChange",false)){
            String mFile = preferences.getString("mFile","");//取出保存的imagePath，若是找不到，则是返回一个空
            //displayImg(mFile);//调用显示图片方法，为ImageView设置图片
            Bitmap photo =BitmapFactory.decodeFile(mFile);
            img.setImageBitmap(photo);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:   //调用相机后返回
                if (resultCode == RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(this, getPackageName()+".provider", tempFile);
                        startPhotoZoom(contentUri);//开始对图片进行裁剪处理
                    } else {
                        startPhotoZoom(Uri.fromFile(tempFile));//开始对图片进行裁剪处理
                    }
                }
                break;
            case ALBUM_REQUEST_CODE:    //调用相册后返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    startPhotoZoom(uri); // 开始对图片进行裁剪处理
                }
                break;
            case CROP_SMALL_PICTURE:  //调用相册剪裁后返回
                if (intent != null) {
                    // 让刚才选择裁剪得到的图片显示在界面上
                    save(mFile);
                    Bitmap photo =BitmapFactory.decodeFile(mFile);
                    img.setImageBitmap(photo);
                } else {
                    Log.e("data","data为空");
                }
                break;
        }

    }
    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {

        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
//        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", false);
        File out = new File(getPath());
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    public  String getPath() {
        //resize image to thumb
        if (mFile == null) {
            mFile = Environment.getExternalStorageDirectory() + "/" +"wode/"+ "outtemp.png";
        }
        return mFile;
    }
    /**
     * 从相机获取图片
     */
    private void getPicFromCamera() {
        //用于保存调用相机拍照后所生成的文件
        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".png");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName()+".provider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            Log.e("getPicFromCamera", contentUri.toString());
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * 从相册获取图片
     */
    private void getPicFromAlbm() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
    }



}
