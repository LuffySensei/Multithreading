import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyCounter {

    private Lock lock = new ReentrantLock();

    private int count;

    void increment() {
        System.out.print("Let's increment the counter");
        lock.lock();
        try
        {
            System.out.print("Inside the synchronized code");
            int temp = this.getCount();
            count = temp + 1;
        } finally { // so that the unlock is always run even if an expected exception occurs
            lock.unlock();
            System.out.print("Left the synchronized code, the counter got incremented");
        }

    }

    int getCount() {
        return count;
    }
}