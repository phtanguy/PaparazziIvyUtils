package eu.telecom_bretagne.project3i.ivy;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class IconToggleButton extends JToggleButton
{
  //-----------------------------------------------------------------------------
  private static Icon greenIcon, greyIcon;
  private static int  delay = 250;
  static
  {
    try
    {
      greenIcon = new ImageIcon(ImageIO.read(IconToggleButton.class.getResource("GreenIcon.png")));
      greyIcon  = new ImageIcon(ImageIO.read(IconToggleButton.class.getResource("GreyIcon.png")));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  private static final long serialVersionUID = 2671425699285841632L;
  private boolean greenDisplay = false;
  //-----------------------------------------------------------------------------
  public IconToggleButton(String text, boolean selected)
  {
    super(text, selected);
    setIcon(greyIcon);
  }
  //-----------------------------------------------------------------------------
  public void messageReceived()
  {
    new Thread(new Runnable()
               {
                  @Override
                  public void run()
                  {
                    // Si le bouton est déjà allumé, ça sert à rien de refaire le boulot !
                    if(!greenDisplay)
                    {
                      setIcon(greenIcon);
                      greenDisplay = true;
                      try { Thread.sleep(delay); } catch (InterruptedException e) {}
                      greenDisplay = false;
                      setIcon(greyIcon);
                    }
                  }
    }).start();;
    
  }
  //-----------------------------------------------------------------------------
}
