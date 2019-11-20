import org.joda.time.DateTime;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class CallableTest {

    public String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 异步需要等待返回结果.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testCallableFuture() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        //创建一个Callable，3秒后返回String类型
        Callable myCallable1 = new Callable() {
            @Override
            public String call() throws Exception {
                Thread.sleep(3000);
                System.out.println("calld方法执行了");
                return "call方法返回值";
            }

        };
        Callable myCallable2 = new Callable() {
            @Override
            public String call() throws Exception {
                Thread.sleep(2000);
                System.out.println("calld方法执行了");
                return "call方法返回值";
            }

        };
        System.out.println("提交任务之前 " + getStringDate());
        Future future1 = executor.submit(myCallable1);
        Future future2 = executor.submit(myCallable2);

        System.out.println("提交任务之后，获取结果之前 " + getStringDate());
        System.out.println("获取返回值: " + future1.get() + future2.get());
        System.out.println("获取到结果之后 " + getStringDate());
    }

    /**
     * 异步不需要等待返回结果.
     */
    @Test
    public void testRunnableFuture() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable runnable = () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("runnable");
        };
        System.out.println("提交任务之前" + DateTime.now());
        executor.submit(runnable);
        System.out.println("提交任务之后" + DateTime.now());

    }
}
