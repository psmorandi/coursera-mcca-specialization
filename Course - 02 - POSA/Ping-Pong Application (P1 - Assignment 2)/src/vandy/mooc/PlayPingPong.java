package vandy.mooc;

import java.util.concurrent.CyclicBarrier;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * @class PlayPingPong
 *
 * @brief This class uses elements of the Android HaMeR framework to
 *        create two Threads that alternately print "Ping" and "Pong",
 *        respectively, on the display.
 */
public class PlayPingPong implements Runnable {
    /**
     * Keep track of whether a Thread is printing "ping" or "pong".
     */
    private enum PingPong {
        PING, PONG
    };

    /**
     * Number of iterations to run the ping-pong algorithm.
     */
    private final int mMaxIterations;

    /**
     * The strategy for outputting strings to the display.
     */
    private final OutputStrategy mOutputStrategy;

    /**
     * Define a pair of Handlers used to send/handle Messages via the
     * HandlerThreads.
     */
    // @@ TODO - you fill in here.
    private Handler[] handlers = new Handler[PingPong.values().length];
    
    private static final int PING_HANDLER = 0;
    private static final int PONG_HANDLER = 1;
    private static final int QUIT_LOOP = 1;

    /**
     * Define a CyclicBarrier synchronizer that ensures the
     * HandlerThreads are fully initialized before the ping-pong
     * algorithm begins.
     */
    // @@ TODO - you fill in here.
    private CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    /**
     * Implements the concurrent ping/pong algorithm using a pair of
     * Android Handlers (which are defined as an array field in the
     * enclosing PlayPingPong class so they can be shared by the ping
     * and pong objects).  The class (1) extends the HandlerThread
     * superclass to enable it to run in the background and (2)
     * implements the Handler.Callback interface so its
     * handleMessage() method can be dispatched without requiring
     * additional subclassing.
     */
    class PingPongThread extends HandlerThread implements Handler.Callback {
        /**
         * Keeps track of whether this Thread handles "pings" or
         * "pongs".
         */
        private PingPong mMyType;

        /**
         * Number of iterations completed thus far.
         */
        private int mIterationsCompleted;

        /**
         * Constructor initializes the superclass and type field
         * (which is either PING or PONG).
         */
        public PingPongThread(PingPong myType) {
        	super(myType.toString());
            // @@ TODO - you fill in here.
        	this.mMyType = myType;
        }

        /**
         * This hook method is dispatched after the HandlerThread has
         * been started.  It performs ping-pong initialization prior
         * to the HandlerThread running its event loop.
         */
        @Override    
        protected void onLooperPrepared() {
            // Create the Handler that will service this type of
            // Handler, i.e., either PING or PONG.
            // @@ TODO - you fill in here.
        	if(mMyType == PingPong.PING){
        		handlers[PING_HANDLER] = new Handler(this);
        	} else {
        		handlers[PONG_HANDLER] = new Handler(this);
        	}

            try {
                // Wait for both Threads to initialize their Handlers.
                // @@ TODO - you fill in here.
            	cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Start the PING_THREAD first by (1) creating a Message
            // where the PING Handler is the "target" and the PONG
            // Handler is the "obj" to use for the reply and (2)
            // sending the Message to the PING_THREAD's Handler.
            // @@ TODO - you fill in here.
            if(mMyType == PingPong.PING){
            	Message ping = handlers[PING_HANDLER].obtainMessage(0, handlers[PONG_HANDLER]);
            	handlers[PING_HANDLER].sendMessage(ping);
            }
        }

        /**
         * Hook method called back by HandlerThread to perform the
         * ping-pong protocol concurrently.
         */
        @Override
        public boolean handleMessage(Message reqMsg) {
            // Print the appropriate string if this thread isn't done
            // with all its iterations yet.
            // @@ TODO - you fill in here, replacing "true" with the
            // appropriate code.
            if (mIterationsCompleted < mMaxIterations) {
            	mIterationsCompleted++;
            	mOutputStrategy.print(System.getProperty("line.separator") + mMyType.toString() + "(" + String.valueOf(mIterationsCompleted)+")");
            } else {
                // Shutdown the HandlerThread to the main PingPong
                // thread can join with it.
                // @@ TODO - you fill in here.
            	
            	//call looper to quit, but before, send an empty message to the other handler,
            	//so it can shut itself down too.
            	if(reqMsg.what != QUIT_LOOP){
            		Handler handler = (Handler)reqMsg.obj;
            		handler.sendEmptyMessage(QUIT_LOOP);
            	}
            	Looper.myLooper().quit();
            	return true;
            }

            // Create a Message that contains the Handler as the
            // reqMsg "target" and our Handler as the "obj" to use for
            // the reply.
            // @@ TODO - you fill in here.
            Handler handler = (Handler)reqMsg.obj;
            Message msg = handler.obtainMessage(0, reqMsg.getTarget());

            // Return control to the Handler in the other
            // HandlerThread, which is the "target" of the msg
            // parameter.
            // @@ TODO - you fill in here.
            handler.sendMessage(msg);

            return true;
        }
    }

    /**
     * Constructor initializes the data members.
     */
    public PlayPingPong(int maxIterations,
                        OutputStrategy outputStrategy) {
        // Number of iterations to perform pings and pongs.
        mMaxIterations = maxIterations;

        // Strategy that controls how output is displayed to the user.
        mOutputStrategy = outputStrategy;
    }

    /**
     * Start running the ping/pong code, which can be called from a
     * main() method in a Java class, an Android Activity, etc.
     */
    public void run() {
        // Let the user know we're starting. 
        mOutputStrategy.print("Ready...Set...Go!");
       
        // Create the ping and pong threads.
        // @@ TODO - you fill in here.
        
        //local array of two threads
        PingPongThread[] playPingPongArray = new PingPongThread[2];
        playPingPongArray[PING_HANDLER] = new PingPongThread(PingPong.PING);
        playPingPongArray[PONG_HANDLER] = new PingPongThread(PingPong.PONG);

        // Start ping and pong threads, which cause their Looper to
        // loop.
        // @@ TODO - you fill in here.
        for (PingPongThread pingPongThread : playPingPongArray) {
			pingPongThread.start();
		}

        // Barrier synchronization to wait for all work to be done
        // before exiting play().
        // @@ TODO - you fill in here.
        
        //join all threads
        try {
        	for (PingPongThread pingPongThread : playPingPongArray) {
    			pingPongThread.join();
    		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  

        // Let the user know we're done.
        mOutputStrategy.print(System.getProperty("line.separator") + "Done!");
    }
}
