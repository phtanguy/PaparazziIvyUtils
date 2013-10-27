package eu.telecom_bretagne.project3i.ivy;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

public class PaparazziIvyUtils
{
	//-----------------------------------------------------------------------------
	private String applicationName = "Paparazzi Ivy Utils";

	private JFrame           frmPaparazziIvyUtils;
	private JTextArea        ivyLogsArea;
	private JButton          btnUnbind;
  private JButton          btnSendMessage;
  private JTextField       tfMessage;
  private JLabel           lblDomainBus;
	private IconToggleButton tglbtnGCS;
	private IconToggleButton tglbtnServer;
  private IconToggleButton tglbtnSim;
  private IconToggleButton tglbtnUav3i;
	private JButton          btnAll;

  private boolean tglbtnGCSActive, tglbtnServerActive, tglbtnSimActive, tglbtnUav3iActive = false;
	
	private Ivy                 bus;
  private String              domainBus;
	private AllMessagesListener allMessagesListener;
	private boolean             bound;
	//-----------------------------------------------------------------------------
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					PaparazziIvyUtils window = new PaparazziIvyUtils();
					window.frmPaparazziIvyUtils.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	//-----------------------------------------------------------------------------
	/**
	 * Create the application.
	 * @throws IvyException 
	 */
	public PaparazziIvyUtils() throws IvyException
	{
	  try
	  {
      initializeIvy(getIvyDomainBus());
      initializeUI();
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(null,
                                     "<html>Problème :<br/>" + e.getMessage(),
                                     "Error",
                                     JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
      System.exit(-1);
    }
	}
  //-----------------------------------------------------------------------------
  private String  getIvyDomainBus() throws Exception
  {
    Properties props = new Properties();
    props.load(new FileInputStream("IvyDomain.properties"));
    domainBus = props.getProperty("IVY_DOMAIN_BUS");
    if (domainBus == null)
      throw new Exception("La clé <i>IVY_DOMAIN_BUS</i> n'a pas été trouvée dans le fichier <i>IvyDomain.properties</i>");
    if(domainBus.length() == 0 || domainBus.equalsIgnoreCase("null"))
      return null;
    else
      return domainBus;
  }
  //-----------------------------------------------------------------------------
	/**
	 * Initailize the connection to the Ivy bus.
	 * @throws IvyException 
	 */
	private void initializeIvy(String domain) throws IvyException
	{
    // initialization, name and ready message
    bus = new Ivy(applicationName,
    		          applicationName + " Ready",
    		          null);
    bus.start(domain);

		// Initialisation of the main Ivy listener.
		allMessagesListener = new AllMessagesListener();
		
		// Not bound when the application is lanched.
		bound = false;
	}
	//-----------------------------------------------------------------------------
	private void bind()
	{
		bound = true;
    try
    {
      bus.bindMsg("(.*)", allMessagesListener);
    }
    catch (IvyException e1)
    {
      e1.printStackTrace();
    }
	}
  //-----------------------------------------------------------------------------
	private void displayBindedButtons()
	{
    // Le texte des boutons s'affiche en gras indiquant que l'on est "bindé" sur le bus Ivy
    tglbtnGCS.setFont(new Font("Dialog", Font.BOLD, 10));
    tglbtnServer.setFont(new Font("Dialog", Font.BOLD, 10));
    tglbtnSim.setFont(new Font("Dialog", Font.BOLD, 10));
    tglbtnUav3i.setFont(new Font("Dialog", Font.BOLD, 10));
	}
  //-----------------------------------------------------------------------------
  private void displayUnBindedButtons()
  {
    // Le texte des boutons s'affiche en italique indiquant que l'on n'est pas/plus "bindé" sur le bus Ivy
    tglbtnGCS.setFont(new Font("Dialog", Font.ITALIC, 10));
    tglbtnServer.setFont(new Font("Dialog", Font.ITALIC, 10));
    tglbtnSim.setFont(new Font("Dialog", Font.ITALIC, 10));
    tglbtnUav3i.setFont(new Font("Dialog", Font.ITALIC, 10));
  }
  //-----------------------------------------------------------------------------
	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeUI()
	{
		// ------- Main frame -------
		
		frmPaparazziIvyUtils = new JFrame();
		frmPaparazziIvyUtils.setTitle(applicationName);
		//frmPaparazziIvyTools.setBounds(100, 100, 550, 900);
		frmPaparazziIvyUtils.setSize(650, 900);
		frmPaparazziIvyUtils.setLocationRelativeTo(null);
		frmPaparazziIvyUtils.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPaparazziIvyUtils.getContentPane().setLayout(new BorderLayout(0, 5));
		
		// ------- Display logs -------

		ivyLogsArea = new JTextArea();
		ivyLogsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		//ivyLogsArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(ivyLogsArea);
		frmPaparazziIvyUtils.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		
		// ------- Panels -------

		JPanel panelCommands = new JPanel();
		frmPaparazziIvyUtils.getContentPane().add(panelCommands, BorderLayout.SOUTH);
		panelCommands.setLayout(new GridLayout(2, 1, 0, 5));
		
		JPanel panelButtons = new JPanel();
		panelCommands.add(panelButtons);
		
		JPanel panelSendMessages = new JPanel();
		panelCommands.add(panelSendMessages);
		panelSendMessages.setLayout(new BorderLayout(0, 0));

		// ------- Buttons -------

		JButton btnClear = new JButton("Clear");
		btnClear.setFont(new Font("Dialog", Font.BOLD, 10));
		btnClear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ivyLogsArea.setText("");
			}
		});
		
		btnUnbind = new JButton("Unbind");
    btnUnbind.setFont(new Font("Dialog", Font.BOLD, 10));
		btnUnbind.setEnabled(false);
		btnUnbind.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
        // Drapeaux indiquant que l'on ne veut rien afficher
        tglbtnGCSActive = tglbtnServerActive = tglbtnSimActive = tglbtnUav3iActive = false;
				bound = false;

				// Les boutons sont désélectionnés
				tglbtnGCS.setSelected(false);
				tglbtnServer.setSelected(false);
				tglbtnSim.setSelected(false);
        tglbtnUav3i.setSelected(false);

        // Le texte des boutons s'affiche en italique indiquant que l'on n'est pas/plus "bindé" sur le bus Ivy
        displayUnBindedButtons();

				bus.unBindMsg("(.*)");
				btnUnbind.setEnabled(false);
			}
		});

		tglbtnGCS = new IconToggleButton("GCS", tglbtnGCSActive);
    tglbtnGCS.setFont(new Font("Dialog", Font.ITALIC, 10));
		tglbtnGCS.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!tglbtnGCSActive)
				{
					tglbtnGCSActive = true;
					btnUnbind.setEnabled(true);
					if(!bound)
						bind();
					displayBindedButtons();
				}
				else
				{
					tglbtnGCSActive = false;
				}
			}
		});

		tglbtnServer = new IconToggleButton("Server", tglbtnServerActive);
    tglbtnServer.setFont(new Font("Dialog", Font.ITALIC, 10));
		tglbtnServer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!tglbtnServerActive)
				{
					tglbtnServerActive = true;
					btnUnbind.setEnabled(true);
					if(!bound)
						bind();
          displayBindedButtons();
				}
				else
				{
					tglbtnServerActive = false;
				}
			}
		});

		tglbtnSim = new IconToggleButton("Sim", tglbtnSimActive);
    tglbtnSim.setFont(new Font("Dialog", Font.ITALIC, 10));
		tglbtnSim.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!tglbtnSimActive)
				{
					tglbtnSimActive = true;
					btnUnbind.setEnabled(true);
					if(!bound)
						bind();
          displayBindedButtons();
				}
				else
				{
					tglbtnSimActive = false;
				}
			}
		});

    tglbtnUav3i = new IconToggleButton("uav3i", tglbtnUav3iActive);
    tglbtnUav3i.setFont(new Font("Dialog", Font.ITALIC, 10));
    tglbtnUav3i.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(!tglbtnUav3iActive)
        {
          tglbtnUav3iActive = true;
          btnUnbind.setEnabled(true);
          if(!bound)
            bind();
          displayBindedButtons();
        }
        else
        {
          tglbtnUav3iActive = false;
        }
      }
    });

    btnAll = new JButton("All");
		btnAll.setFont(new Font("Dialog", Font.BOLD, 10));
		btnAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
        btnUnbind.setEnabled(true);
        if(!bound)
          bind();
        
        // Drapeaux indiquant que l'on veut tout afficher
        tglbtnGCSActive = tglbtnServerActive = tglbtnSimActive = tglbtnUav3iActive = true;

        // Les boutons sont sélectionnés
        tglbtnGCS.setSelected(true);
        tglbtnServer.setSelected(true);
        tglbtnSim.setSelected(true);
        tglbtnUav3i.setSelected(true);

        // Le texte des boutons s'affiche en gras indiquant que l'on est "bindé" sur le bus Ivy
        displayBindedButtons();
			}
		});
		panelButtons.setLayout(new GridLayout(0, 7, 0, 0));

		panelButtons.add(btnClear);
		panelButtons.add(btnUnbind);
		panelButtons.add(tglbtnGCS);
		panelButtons.add(tglbtnServer);
    panelButtons.add(tglbtnSim);
    panelButtons.add(tglbtnUav3i);
		panelButtons.add(btnAll);
		
		tfMessage = new JTextField();
		tfMessage.setHorizontalAlignment(SwingConstants.CENTER);
		tfMessage.setFont(new Font("Consolas", Font.BOLD, 11));
		panelSendMessages.add(tfMessage, BorderLayout.CENTER);
		tfMessage.setColumns(10);
		
		btnSendMessage = new JButton("Send");
		btnSendMessage.setHorizontalAlignment(SwingConstants.RIGHT);
		btnSendMessage.setFont(new Font("Dialog", Font.BOLD, 10));
		btnSendMessage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
        {
	        bus.sendMsg(tfMessage.getText());
        }
        catch (IvyException e1)
        {
	        e1.printStackTrace();
        }
			}
		});
		panelSendMessages.add(btnSendMessage, BorderLayout.EAST);
		
		lblDomainBus = new JLabel("<html>Ivy Domain Bus: <i>" + domainBus + "</i>");
		lblDomainBus.setFont(new Font("Dialog", Font.BOLD, 10));
		lblDomainBus.setHorizontalAlignment(SwingConstants.LEFT);
		panelSendMessages.add(lblDomainBus, BorderLayout.NORTH);
	}
	//-----------------------------------------------------------------------------

	
	
	
	
	
	
	//-----------------------------------------------------------------------------
	private class AllMessagesListener implements IvyMessageListener
	{
		@Override
    public void receive(IvyClient client, String[] args)
    {
		  //System.out.println("Je reçois quelque chose... " + args[0]);
			String applicationName = client.getApplicationName();

			boolean display = false;
			
      if(applicationName.startsWith("Paparazzi GCS"))
      {
        tglbtnGCS.messageReceived();
        display = tglbtnGCSActive;
      }
      else if(applicationName.startsWith("Paparazzi server"))
      {
        tglbtnServer.messageReceived();
        display = tglbtnServerActive;
      }
      else if(applicationName.startsWith("Paparazzi sim"))
      {
        tglbtnSim.messageReceived();
        display = tglbtnSimActive;
      }
      else if(applicationName.startsWith("uav3i"))
      {
        tglbtnUav3i.messageReceived();
        display = tglbtnUav3iActive;
      }
			
			if(display)
			{
				String format = "%1$-20s| %2$s\n";
				ivyLogsArea.append(String.format(format, applicationName, args[0]));
				ivyLogsArea.setCaretPosition(ivyLogsArea.getText().length());
			}
    }
	}
	//-----------------------------------------------------------------------------
}
