package ticket_script;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 测试取反操作
 * @author Administrator
 *
 */
public class EncriptUtil {

    /**
     * @param args
     */
    public static void main(String[] args) {
        encrpt("C:/Users/Crap/eclipse-cm/ticket_script/target/classes/application/AppInfo.class","C:/Users/Crap/eclipse-cm/ticket_script/target/classes/application/s/AppInfo.class");
    }
    /**
     * 测试的是取反
     * @param src
     * @param dest
     */
    public static void encrpt(String src,String dest){
        FileInputStream fis=null;
        FileOutputStream fos=null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);
            int temp =-1;
            while((temp=fis.read())!=-1){
                fos.write(temp^0xff);//读出的数据进行取反；
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(null!=fis){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
