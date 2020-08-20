import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClassName main
 * Description
 * Author Ka1HuangZhe
 * Date  8/20/2020
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Logger log = Logger.getLogger("log");
        log.setLevel(Level.INFO);
        log.info("文件名：" + args[0]);
        log.info("文件后缀名：" + args[1]);

        log.info("word : " +VerificationCodeByBaiduSDK.getVCode("test2.png", "png" ));
    }
}
