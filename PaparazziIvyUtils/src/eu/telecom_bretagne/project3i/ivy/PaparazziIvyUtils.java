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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

public class PaparazziIvyUtils
{
	//-----------------------------------------------------------------------------
	private String applicationName = "Paparazzi Ivy Utils";

	private JFrame        frmPaparazziIvyUtils;
	private JTextArea     ivyLogsArea;
	private JButton       btnUnbind;
  private JButton       btnSendMessage;
  private JTextField    tfMessage;
  private JLabel        lblDomainBus;
	private JToggleButton tglbtnGCS;
	private JToggleButton tglbtnServer;
  private JToggleButton tglbtnSim;
  private JToggleButton tglbtnUav3i;
	private JToggleButton tglbtnAll;

	private boolean tglbtnGCSActive, tglbtnServerActive, tglbtnSimActive, tglbtnUav3iActive, tglbtnAllActive  = false;
	
	private Ivy                  bus;
  private String               domainBus;
	private AllMessagesListener allMessagesListener;
	private boolean              bound;
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
	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeUI()
	{
		// ------- Main frame -------
		
		frmPaparazziIvyUtils = new JFrame();
		frmPaparazziIvyUtils.setTitle(applicationName);
		//frmPaparazziIvyTools.setBounds(100, 100, 550, 900);
		frmPaparazziIvyUtils.setSize(550, 900);
		frmPaparazziIvyUtils.setLocationRelativeTo(null);
		frmPaparazziIvyUtils.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPaparazziIvyUtils.getContentPane().setLayout(new BorderLayout(0, 5));
		
		// ------- Display logs -------

		ivyLogsArea = new JTextArea();
		ivyLogsArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
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
				tglbtnGCSActive = tglbtnServerActive = tglbtnSimActive = tglbtnAllActive = false;
				bound = false;
				tglbtnGCS.setSelected(false);
				tglbtnServer.setSelected(false);
				tglbtnSim.setSelected(false);
        tglbtnUav3i.setSelected(false);
				tglbtnAll.setSelected(false);
				
				bus.unBindMsg("(.*)");
				btnUnbind.setEnabled(false);
			}
		});

		tglbtnGCS = new JToggleButton("GCS", tglbtnGCSActive);
		tglbtnGCS.setFont(new Font("Dialog", Font.BOLD, 10));
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
				}
				else
				{
					tglbtnGCSActive = false;
				}
			}
		});

		tglbtnServer = new JToggleButton("Server", tglbtnServerActive);
		tglbtnServer.setFont(new Font("Dialog", Font.BOLD, 10));
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
				}
				else
				{
					tglbtnServerActive = false;
				}
			}
		});

		tglbtnSim = new JToggleButton("Sim", tglbtnSimActive);
		tglbtnSim.setFont(new Font("Dialog", Font.BOLD, 10));
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
				}
				else
				{
					tglbtnSimActive = false;
				}
			}
		});

    tglbtnUav3i = new JToggleButton("uav3i", tglbtnUav3iActive);
    tglbtnUav3i.setFont(new Font("Dialog", Font.BOLD, 10));
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
        }
        else
        {
          tglbtnUav3iActive = false;
        }
      }
    });

		tglbtnAll = new JToggleButton("All", tglbtnAllActive);
		tglbtnAll.setFont(new Font("Dialog", Font.BOLD, 10));
		tglbtnAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!tglbtnAllActive)
				{
					tglbtnAllActive = true;
					btnUnbind.setEnabled(true);
					if(!bound)
						bind();
				}
				else
				{
					tglbtnAllActive = false;
				}
			}
		});
		panelButtons.setLayout(new GridLayout(0, 7, 0, 0));

		panelButtons.add(btnClear);
		panelButtons.add(btnUnbind);
		panelButtons.add(tglbtnGCS);
		panelButtons.add(tglbtnServer);
    panelButtons.add(tglbtnSim);
    panelButtons.add(tglbtnUav3i);
		panelButtons.add(tglbtnAll);
		
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
		  System.out.println("Je reçois quelque chose... " + args[0]);
			String applicationName = client.getApplicationName();

			boolean display =    (applicationName.startsWith("Paparazzi GCS") && tglbtnGCSActive)
                        || (applicationName.startsWith("Paparazzi server") && tglbtnServerActive)
                        || (applicationName.startsWith("Paparazzi sim") && tglbtnSimActive)
                        || (applicationName.startsWith("uav3i") && tglbtnUav3iActive)
                        || tglbtnAllActive;
			
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
