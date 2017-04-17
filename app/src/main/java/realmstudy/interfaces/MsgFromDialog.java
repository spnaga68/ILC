package realmstudy.interfaces;

import java.util.ArrayList;

/**
 * Created by developer on 23/2/17.
 */
public interface MsgFromDialog {
    void messageFromDialog(int dialogType,boolean success,String data,String message);
    void messageFromDialog(int dialogType, boolean success, ArrayList<Integer> data, String message);
    void messageFromDialog(int dialogType,boolean success,String data,String message,int assignTo);

}
