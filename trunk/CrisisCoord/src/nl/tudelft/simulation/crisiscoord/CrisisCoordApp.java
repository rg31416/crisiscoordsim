/*
 * @(#) CrisisCoordApp.java Jun 30, 2008
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord;

import java.net.URL;
import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.experiment.ExperimentalFrame;
import nl.tudelft.simulation.dsol.gui.DSOLApplication;
import nl.tudelft.simulation.dsol.gui.panels.GUIExperimentParsingThread;
import nl.tudelft.simulation.event.EventInterface;
import nl.tudelft.simulation.event.EventListenerInterface;
import nl.tudelft.simulation.language.io.URLResource;
import nl.tudelft.simulation.xml.dsol.ExperimentParsingThread;

/**
 * CrisisCoord Application.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @version 1.5 <br>
 * @author <a> Rafael Gonzalez </a>
 */
public class CrisisCoordApp extends DSOLApplication implements EventListenerInterface 
{
	/** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new application.
     * 
     * @param url url with navigation-bar properties
     */
	public CrisisCoordApp(final URL url)
    {
        super(url);
        this.addListener(this, ExperimentParsingThread.EXPERIMENT_PARSED_EVENT);

        new GUIExperimentParsingThread(this, null, URLResource
                .getResource("/crisiscoord.xml")).start();
    }

    /**
     * Notifies
     * @see nl.tudelft.simulation.event.EventListenerInterface#notify(nl.tudelft.simulation.event.EventInterface)
     * @param event reference to an event interface
     * @throws RemoteException throws a remote exception if an error occurs
     */
    public void notify(final EventInterface event) throws RemoteException
    {
        this.setExperimentalFrame((ExperimentalFrame) event.getContent());
    }

    /**
     * Executes the dsol control panel.
     * 
     * @param args
     *            the command-line arguments (none required)
     */
    public static void main(final String[] args)
    {
        URL navigation = null;
        if (args.length != 0)
        {
            navigation = URLResource.getResource(args[0]);
        } else if (System.getProperty("dsol.navigation") != null)
        {
            navigation = URLResource.getResource(System
                    .getProperty("dsol.navigation"));
        }
        if (navigation == null)
        {
            navigation = URLResource.getResource("/navigation.xml");
        }
        new CrisisCoordApp(navigation);
    }
}