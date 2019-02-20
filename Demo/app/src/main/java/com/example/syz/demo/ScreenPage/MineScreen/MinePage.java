package com.example.syz.demo.screenPage.MineScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.syz.demo.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static android.app.Activity.RESULT_OK;

public class MinePage extends Fragment implements View.OnClickListener {


    private ImageView setting_img;
    private ImageView photo_img;
    private ImageView sex_img;
    private TextView name;
    private TextView motto;
    private TextView fans_count;
    private TextView attention_count;
    private TextView grade_count;
    private Button edit_button;
    private View fanTab;
    private View attentionTab;
    private View gradeTab;
    private Uri photoUri;
    private String img;
    private Bitmap photoBitmap;
    private View notification_background;
    private boolean isLogin = false;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mine_page, container, false);
//        Bundle bundle = getActivity().getIntent().getExtras();
//        name.setText(bundle.getString("nickName"));
//        motto.setText(bundle.getString("motto"));
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setting_img.setOnClickListener(this);
        photo_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_button:
                Intent settingIntent = new Intent(getActivity(), SettingAvtivity.class);
                settingIntent.putExtra("login", isLogin);
                startActivity(settingIntent);
                break;
            case R.id.photo_img:
                if (isLogin) {
                    //展示一张图片
                } else {
                    Intent LoginIntent = new Intent(v.getContext(), Loginactivity.class);
                    startActivityForResult(LoginIntent, 3);
                }
                break;
        }
    }

    private void initView(View view) {
        setting_img = (ImageView) view.findViewById(R.id.setting_button);
        photo_img = (ImageView) view.findViewById(R.id.photo_img);
        name = (TextView) view.findViewById(R.id.name);
        motto = (TextView) view.findViewById(R.id.motto);
        fanTab = (TextView) view.findViewById(R.id.fans_count);
        attention_count = (TextView) view.findViewById(R.id.attention_count);
        grade_count = (TextView) view.findViewById(R.id.grade_count);
        edit_button = (Button) view.findViewById(R.id.edit);
        fanTab = (View) view.findViewById(R.id.fans_tab);
        attentionTab = (View) view.findViewById(R.id.attention_tab);
        gradeTab = (View) view.findViewById(R.id.grade_tab);
        sex_img = (ImageView) view.findViewById(R.id.sex_img);
        intentFilter = new IntentFilter();
        intentFilter.addAction("login");
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        if (isLogin) {
            photo_img.setImageBitmap(photoBitmap);
        } else {
            photo_img.setImageResource(R.drawable.head_photo_img);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 3:
//                if (resultCode == RESULT_OK) {
//                    Toast.makeText(getContext(), "nihao", Toast.LENGTH_SHORT).show();
//                    name.setText(data.getStringExtra("nickName"));
//                    motto.setText(data.getStringExtra("motto"));
//                }
//                break;
//            default:
//                Toast.makeText(getActivity(), "没有", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("islogin", false)) {
                isLogin = intent.getBooleanExtra("islogin", false);
                name.setText(intent.getStringExtra("nickName"));
                motto.setText(intent.getStringExtra("motto"));
                if (intent.getStringExtra("sex").equals("男")) {
                    sex_img.setImageResource(R.drawable.boy);
                } else {
                    sex_img.setImageResource(R.drawable.girl);
                }
                img = intent.getStringExtra("img");
                new HttpImageDownload(photo_img, img).execute();
            } else {
                isLogin = intent.getBooleanExtra("islogin", false);
                photo_img.setImageResource(R.drawable.head_photo_img);
                sex_img.setImageDrawable(null);
                name.setText("未登录");
                motto.setText("点击头像登录");
            }
        }
    }



    public class HttpImageDownload extends AsyncTask {

        private ImageView mImageView;
        private String mImgPath;

        public HttpImageDownload(ImageView imageView, String imgPath)
        {
            mImageView = imageView;
            mImgPath = imgPath;
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                // 创建一个URL
                URL url = new URL("http://ww1.sinaimg.cn/large/0077HGE3ly1g0d9el5x7qj30go0aft93.jpg");

                // 从URL获取对应资源的 InputStream
                InputStream inputStream = url.openStream();
                // 用inputStream来初始化一个Bitmap 虽然此处是Bitmap，但是URL不一定非得是Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // 关闭 InputStream
                inputStream.close();

                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            // 此处的形参o，是doInBackground的返回值
            mImageView.setImageBitmap((Bitmap)o);
        }
    }



    /**
     * 模糊图片的具体方法
     *
     * @param context 上下文对象
     * @param image   需要模糊的图片
     * @return 模糊处理后的图片
     */
    class ImageFilter {
        //图片缩放比例
        private static final float BITMAP_SCALE = 0.4f;
        private Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
            // 计算图片缩小后的长宽
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            // 将缩小后的图片做为预渲染的图片
            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            // 创建一张渲染后的输出图片
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            // 创建RenderScript内核对象
            RenderScript rs = RenderScript.create(context);
            // 创建一个模糊效果的RenderScript的工具对象
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
            // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

            // 设置渲染的模糊程度, 25f是最大模糊度
            blurScript.setRadius(blurRadius);
            // 设置blurScript对象的输入内存
            blurScript.setInput(tmpIn);
            // 将输出数据保存到输出内存中
            blurScript.forEach(tmpOut);

            // 将数据填充到Allocation中
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }
}
