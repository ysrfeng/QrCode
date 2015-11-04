package com.ysr.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zxing.activity.CaptureActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button scanButton;
    private TextView text;
    private Button genButton;
    private Button genButton2;
    private ImageView img;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanButton = (Button) findViewById(R.id.bt);
        text = (TextView) findViewById(R.id.tv);
        genButton = (Button) findViewById(R.id.gen);
        genButton2 = (Button) findViewById(R.id.gen2);
        img = (ImageView) findViewById(R.id.ivShow);
        input = (EditText) findViewById(R.id.input);

        genButton2.setOnClickListener(this);

        scanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.this, "你写可以扫描条形码或者二维码", Toast.LENGTH_SHORT).show();
                Intent startScan = new Intent(MainActivity.this, CaptureActivity.class);
                // startActivity(startScan);
                startActivityForResult(startScan, 0);
            }
        });
        genButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String in = input.getText().toString();
                if (in.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入文本", Toast.LENGTH_SHORT).show();
                }
                try {

//TODO 也可以生成二维码
//                    Bitmap qrcod = EncodingHandler.createQRCode(in, 400);
//                    img.setImageBitmap(qrcod);


                    // 写入数据信息到图片
                    int width = 400, height = 400;
                    QRCodeWriter writer = new QRCodeWriter();

                    //把内容编码
                    BitMatrix matrix = writer.encode(in, BarcodeFormat.QR_CODE, width, height);
                    int ms[] = new int[width * height];
                    //变换赋值
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            if (matrix.get(x, y)) {
                                //黑点
                                ms[y * width + x] = 0xff000000;
                            } else {
                                //白点
                                ms[y * width + x] = 0xffffffff;
                            }

                        }

                    }

                    //TODO 缓存
                    Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    image.setPixels(ms, 0, width, 0, 0, width, height);

                    //TODO 展示图片
                    img.setImageBitmap(image);
                    FileOutputStream out = new FileOutputStream("/sdcard/code2.png");
                    //TODO 压缩
                    image.compress(Bitmap.CompressFormat.PNG, 100, out);
                    Log.e("MMMM", "创建成功");


                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getExtras().getString("result");
            text.setText(result);
        }
    }


    //Todo 带logo的二维码
    @Override
    public void onClick(View v) {

        String in = input.getText().toString();
        if (in.equals("")) {
            Toast.makeText(MainActivity.this, "请输入文本", Toast.LENGTH_SHORT).show();
        }
        try {
            // 写入数据信息到图片
            int width = 400, height = 400;
            QRCodeWriter writer = new QRCodeWriter();
            //把内容编码
            BitMatrix matrix = writer.encode(in, BarcodeFormat.QR_CODE, width, height);
            int ms[] = new int[width * height];
            //变换赋值
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        //黑点
                        ms[y * width + x] = 0xff000000;
                    } else {
                        //白点
                        ms[y * width + x] = 0xffffffff;
                    }

                }

            }
            //TODO 缓存
            Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            image.setPixels(ms, 0, width, 0, 0, width, height);
            //logo地址
            Bitmap logo = BitmapFactory.decodeFile("/sdcard/gur03.png");
            image = insertLogo(image, logo);
            //TODO 展示图片
            img.setImageBitmap(image);
            //存储生成的二维码地址
            FileOutputStream out = new FileOutputStream("/sdcard/code2.png");
            //TODO 压缩
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.e("MMMM", "创建成功");

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bitmap insertLogo(Bitmap src, Bitmap logo) {
        // 获取到两张图片的宽高
        int width = src.getWidth();
        int height = src.getHeight();
        int gwidth = logo.getWidth();
        int gheight = logo.getHeight();
        // 大小图片的比例
        float scale = width * 1.0f / 5 / gwidth;
        // 工作缓冲区
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 获取缓冲区的画布
        Canvas ca = new Canvas(bitmap);
        // 话大图片
        ca.drawBitmap(src, 0, 0, null);
        ca.scale(scale, scale, width / 2, height / 2);
        // 话小图片
        ca.drawBitmap(logo, (width - gwidth) / 2, (height - gheight) / 2, null);
        // 保存所画内容
        ca.save(Canvas.ALL_SAVE_FLAG);
        // 还原画布
        ca.restore();
        return bitmap;
    }
}
