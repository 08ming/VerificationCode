import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * ClassName VerificationCodeByBaiduSDK
 * Description
 * Author Ka1HuangZhe
 * Date  8/19/2020
 */
public class VerificationCodeByBaiduSDK {
    //设置APPID/AK/SK
    public static final String APP_ID = "22104205";
    public static final String API_KEY = "h0MkcQSQGaqzoikOIhd3hvfG";
    public static final String SECRET_KEY = "XNlHz6w8UN7AVxj7aOhkjwnAItpxSyS0";

    public static String getVCode(String fp, String fpSuffix) throws IOException {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 请求参数
        HashMap<String, String> options = new HashMap<String, String>();


        // 图像预处理
        BufferedImage bufImage = ImageIO.read(new File(fp));

        // 二值化处理
        bufImage = imageToDualFormatSecond(bufImage);

        // 裁剪
        bufImage = cutImage(bufImage);

        ImageIO.write(bufImage,"png", new File(fp+"result.png"));
        // 转换为字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bufImage, fpSuffix, bos);
        bos.flush();
        byte[] byteImage = bos.toByteArray();

        // 调用接口
        JSONObject res = client.accurateGeneral(byteImage, options);
        System.out.println(res.toString(2));
        JSONArray r =(JSONArray) res.get("words_result");
        HashMap<Object, Object> resultsList = (HashMap<Object, Object>) r.toList().get(0);
        return (String) resultsList.get("words");
    }

    public static BufferedImage cutImage(BufferedImage bufImage){
        //初始化
        bufImage = bufImage.getSubimage(5, 2, bufImage.getWidth() - 5, bufImage.getHeight() - 2);

        //先切割左右，再切割上下
        int col = -1;
        for (int i = 0; i < bufImage.getWidth(); i++) {
            boolean flag = true;
            int head = bufImage.getRGB(i,0);
            for (int i1 = 1; i1 < bufImage.getHeight(); i1++) {
                int ceil = bufImage.getRGB(i,i1);
                if(ceil != head){
                    flag = false;
                    break;
                }
            }
            col ++;
            if(!flag){
                bufImage = bufImage.getSubimage(col, 0, bufImage.getWidth() - col, bufImage.getHeight());
                break;
            }
        }
        col = -1;
        for (int i = bufImage.getWidth() - 1; i >= 0 ; i--) {
            boolean flag = true;
            int head = bufImage.getRGB(i,0);
            for (int i1 = 1; i1 < bufImage.getHeight(); i1++) {
                int ceil = bufImage.getRGB(i,i1);
                if(ceil != head){
                    flag = false;
                    break;
                }
            }
            col ++;
            if(!flag){
                bufImage = bufImage.getSubimage(0, 0, bufImage.getWidth() - col, bufImage.getHeight());
                break;
            }
        }

        int row = -1;
        for (int i = 0; i < bufImage.getHeight(); i++) {
            boolean flag = true;
            int head = bufImage.getRGB(i,0);
            for (int i1 = 1; i1 < bufImage.getWidth(); i1++) {
                int ceil = bufImage.getRGB(i1,i);
                if(ceil != head){
                    flag = false;
                    break;
                }
            }
            row ++;
            if(!flag){
                bufImage = bufImage.getSubimage(0, row, bufImage.getWidth(), bufImage.getHeight() - row);
                break;
            }
        }
        row = -1;
        for (int i = bufImage.getHeight() - 1; i >= 0 ; i--) {
            boolean flag = true;
            int head = bufImage.getRGB(i,0);
            for (int i1 = 1; i1 < bufImage.getWidth(); i1++) {
                int ceil = bufImage.getRGB(i1,i);
                if(ceil != head){
                    flag = false;
                    break;
                }
            }
            row ++;
            if(!flag){
                bufImage = bufImage.getSubimage(0, 0, bufImage.getWidth(), bufImage.getHeight() - row);
                break;
            }
        }

        return bufImage;
    }

    public static BufferedImage imageToDualFormatSecond(BufferedImage img){
        int threshold = 500;
        int width = img.getWidth();
        int height = img.getHeight();
        for(int i = 1;i < width;i++){
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){
                    Color color = new Color(img.getRGB(x, y));
                    int num = color.getRed()+color.getGreen()+color.getBlue();
                    if(num >= threshold){
                        img.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
        }
        for(int i = 1;i<width;i++){
            Color color1 = new Color(img.getRGB(i, 1));
            int num1 = color1.getRed()+color1.getGreen()+color1.getBlue();
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    Color color = new Color(img.getRGB(x, y));

                    int num = color.getRed()+color.getGreen()+color.getBlue();
                    if(num==num1){
                        img.setRGB(x, y, Color.BLACK.getRGB());
                    }else{
                        img.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
        }
        return img;
    }


    public static BufferedImage imageToDualFormat(BufferedImage bi){
        int h = bi.getHeight();
        int w = bi.getWidth();
        int rgbs[][] = new int[w][h];

        // 获取图片每一像素点的灰度值
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                // getRGB()返回默认的RGB颜色模型(十进制)
                rgbs[i][j] = getImageRgb(bi.getRGB(i, j));//该点的灰度值
            }
        }

        BufferedImage resultIBi=new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);//  构造一个类型为预定义图像类型之一的 BufferedImage，TYPE_BYTE_BINARY（表示一个不透明的以字节打包的 1、2 或 4 位图像。）
        // 阈值
        int FZ=215;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if(getGray(rgbs,i,j,w,h)>FZ){
                    int black=new Color(255,255,255).getRGB();
                    resultIBi.setRGB(i, j, black);
                }else{
                    int white=new Color(0,0,0).getRGB();
                    resultIBi.setRGB(i, j, white);
                }
            }

        }

        return resultIBi;
    }

    private static int getImageRgb(int i) {
        String argb = Integer.toHexString(i);// 将十进制的颜色值转为十六进制
        // argb分别代表透明,红,绿,蓝 分别占16进制2位
        int r = Integer.parseInt(argb.substring(2, 4),16);//后面参数为使用进制
        int g = Integer.parseInt(argb.substring(4, 6),16);
        int b = Integer.parseInt(argb.substring(6, 8),16);
        int result=(int)((r+g+b)/3);
        return result;
    }



    //自己加周围8个灰度值再除以9，算出其相对灰度值
    public static int  getGray(int gray[][], int x, int y, int w, int h)
    {
        int rs = gray[x][y]
                + (x == 0 ? 255 : gray[x - 1][y])
                + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
                + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])
                + (y == 0 ? 255 : gray[x][y - 1])
                + (y == h - 1 ? 255 : gray[x][y + 1])
                + (x == w - 1 ? 255 : gray[x + 1][ y])
                + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
                + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }
}
