package kernel.legacy;

import kernel.KernelException;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.Collection;
import java.util.ArrayList;

import rescuecore2.config.Config;
import rescuecore2.connection.Connection;
import rescuecore2.connection.ConnectionException;
import rescuecore2.connection.TCPConnection;
import rescuecore2.connection.ConnectionListener;
import rescuecore2.messages.Message;
import rescuecore2.messages.control.GKConnectOK;
import rescuecore2.messages.control.GKConnectError;
import rescuecore2.messages.control.KGConnect;
import rescuecore2.messages.control.KGAcknowledge;
import rescuecore2.worldmodel.Entity;

import rescuecore2.version0.entities.RescueEntity;
import rescuecore2.version0.entities.RescueWorldModel;

/**
   A WorldModelCreator that talks to the GIS to build RescueEntities.
 */
public class LegacyWorldModelCreator {
    /**
       Create a new RescueWorldModel.
       @param config The config to use.
       @return A new world model.
       @throws KernelException If there is a problem building the world model.
    */
    public RescueWorldModel buildWorldModel(Config config) throws KernelException {
        System.out.println("Connecting to GIS...");
        RescueWorldModel world = new RescueWorldModel(config.getIntValue("vision"));
        CountDownLatch latch = new CountDownLatch(1);
        int gisPort = config.getIntValue("gis_port");
        Connection conn;
        try {
            conn = new TCPConnection(gisPort);
            conn.addConnectionListener(new GISConnectionListener(latch, world));
            conn.startup();
            conn.sendMessage(new KGConnect());
        }
        catch (IOException e) {
            throw new KernelException("Couldn't connect to GIS", e);
        }
        catch (ConnectionException e) {
            throw new KernelException("Couldn't connect to GIS", e);
        }
        // Wait for a reply
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            throw new KernelException("Interrupted while connecting to GIS", e);
        }
        conn.shutdown();
        world.index();
        return world;
    }

    /**
       Listener for the GIS connection.
    */
    private static class GISConnectionListener implements ConnectionListener {
        private CountDownLatch latch;
        private RescueWorldModel model;

        public GISConnectionListener(CountDownLatch latch, RescueWorldModel model) {
            this.latch = latch;
            this.model = model;
        }

        public void messageReceived(Connection c, Message m) {
            if (m instanceof GKConnectOK) {
                try {
                    // Update the internal world model
                    model.removeAllEntities();
                    model.addEntities(((GKConnectOK)m).getEntities());
                    // Send an acknowledgement
                    c.sendMessage(new KGAcknowledge());
                    System.out.println("GIS connected OK");
                    // Trigger the countdown latch
                    latch.countDown();
                }
                catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
            if (m instanceof GKConnectError) {
                System.err.println("Error: " + ((GKConnectError)m).getReason());
            }
        }
    }
}