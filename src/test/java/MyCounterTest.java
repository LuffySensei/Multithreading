import com.google.testing.threadtester.*;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;


public class MyCounterTest {

    @Test
    public void testThreading() {
        ThreadedTestRunner runner = new ThreadedTestRunner();
        // Run all Weaver tests in this class, using MyCounter as the Class Under Test.
        runner.runTests(this.getClass(), MyCounter.class);
    }

    @ThreadedTest
    public void testIncrement() throws Exception {
        final MyCounter myCounter = new MyCounter();
        Runnable task = new Runnable() {
            public void run() { myCounter.increment(); }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        // Create a Breakpoint from the code position and thread.
        ObjectInstrumentation<MyCounter> instrumented =
                Instrumentation.getObjectInstrumentation(myCounter);
        Breakpoint bp = instrumented.createBreakpoint(getCodePosition(), thread1);

        // Start the thread. It will run until it hits the Breakpoint
        thread1.start();

        // Wait until the breakpoint is reached. When we return from
        // await, the first thread will be at 'position'
        bp.await();

        thread2.start();
        ThreadMonitor monitor = new ThreadMonitor(thread2, thread1);

        // Wait for the second thread to finish. Returns false if the second
        // thread is blocked, or true if it ran to completion

        Assert.assertEquals(monitor.waitForThread(), false);

        // Let the first thread continue.
        bp.resume();
        // Wait for the first thread to finish
        thread1.join();

        // Wait for second thread to finish.
        thread2.join();

        Assert.assertEquals(monitor.waitForThread(), true);
    }


    private CodePosition getCodePosition() throws NoSuchMethodException, NoSuchFieldException {
        ClassInstrumentation instr = Instrumentation.getClassInstrumentation(MyCounter.class);
        Method increment = MyCounter.class.getDeclaredMethod("increment");
        Method getCounter = MyCounter.class.getDeclaredMethod("getCount");
        return instr.beforeCall(increment, getCounter); // stop before getCounter called
    }

}
