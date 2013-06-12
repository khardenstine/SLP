package altitourny.slp;

// Code adapted from lamster
// Edited by: Michael Solomon
// 2-5-11
//
// Edited by: Karl Hardenstine
// June-2013

public class JThreadHelper
{
    // Does what you'd imagine - puts the thread to sleep for some number of ms.
    public static void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            SLP.getLog().error(e.getMessage());
        }
    }

    // The virtual machine exits when all remaining threads are daemon
    // In other words this thread can't keep the virtual machine alive
    public static Thread createDaemonThread(Runnable runnable)
    {
        Thread thread = new Thread(runnable);
        thread.setDaemon(false);
        return thread;
    }

    public static Thread startDaemonThread(Runnable runnable)
    {
        Thread thread = createDaemonThread(runnable);
        thread.start();
        return thread;
    }

    // Causes the currently executing thread object to temporarily pause and allow other threads to execute.
    public static void yield() {
        Thread.yield();
    }
}