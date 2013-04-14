/*******************************************************
 CS451 Multimedia Software Systems
 @ Author: Elaine Kang
 *******************************************************/

// Template Code

public class CS451_Main
{
  public static void main(String[] args)
  {
    ImageModel model = new ImageModel();
    ImageView view = new ImageView(model);
    ImageCtrl controller = new ImageCtrl(model, view);
  }
}