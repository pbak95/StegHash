package pl.pb.steganography.LSB;

/**
 * Created by Patryk on 2017-10-08.
 */
public class LsbTest {
    public static void main(String[] args) {
//        try {
//            FileReader code = new FileReader("D:\\dyplom\\stegTests\\texts\\testMessage.txt");
//            BufferedReader in = new BufferedReader(code);
//            String s = "";
//            String g = "";
//            while((s=in.readLine())!=null)
//                g+=s;
//
//            Convert c = new Convert();
//            ImageProcess impro = new ImageProcess();
//            byte [] txtBytes = c.txtToByte(g);
//            BufferedImage img = impro.fetchImage();
//            impro.hideText(img,txtBytes);
//            //c.conToBinary(g);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
        LSBMethod lsb = new LSBMethod();
        String msg = "Simple message234";
        try {
            lsb.setMessage("lion.png", msg);
            System.out.println("Text is: " + lsb.getMessage("lion_steg.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
