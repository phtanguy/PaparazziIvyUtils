package eu.telecom_bretagne.project3i.ivy;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

public class IvyFirstTest implements IvyMessageListener
{
	//-----------------------------------------------------------------------------
	private Ivy bus;
	//-----------------------------------------------------------------------------
	public IvyFirstTest() throws IvyException
	{
    // initialization, name and ready message
    bus = new Ivy("IvyTranslater","IvyTranslater Ready",null);
    
    // classical subscription
    bus.bindMsg("^Hello(.*)",this);
    
    // inner class subscription ( think awt )
		bus.bindMsg("^Bye$", new IvyMessageListener()
		{
			public void receive(IvyClient client, String[] args)
			{
				// leaves the bus, and as it is the only thread, quits
				bus.stop();
			}
		});
    bus.start(null); // starts the bus on the default domain		
	}
	//-----------------------------------------------------------------------------
	/*
	 * Callback associated to the "Hello" messages"
	 */
	@Override
  public void receive(IvyClient client, String[] args)
  {
		try
		{
			bus.sendMsg("Bonjour" + ((args.length > 0) ? args[0] : ""));
		}
		catch (IvyException ie)
		{
			System.out.println("can't send my message on the bus");
		}
  }
	//-----------------------------------------------------------------------------
	public static void main(String[] args) throws IvyException
	{
		new IvyFirstTest();
	}
	//-----------------------------------------------------------------------------
}
