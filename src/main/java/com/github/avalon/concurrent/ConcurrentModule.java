package com.github.avalon.concurrent;

import com.github.avalon.annotation.annotation.Module;
import com.github.avalon.module.AbstractModule;
import com.github.avalon.server.IServer;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * This manager manages the all created threads for {@link AbstractModule}. These threads are used
 * for asynchronous task execution. We can specify if we want to use asynchronous thread or not in
 * {@link Module} annotation. Threads are split by their priority respectively by {@link
 * AbstractModule} priority. If there is already a thread with same priority, then this thread is
 * assigned to the manager. But these situations usually should not happen.
 *
 * @author Horizon
 * @version 1.0
 */
public class ConcurrentModule extends AbstractModule<IServer> {

  public static final int CORE_POOL_SIZE = 1;
  public static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
  public static final int KEEP_ALIVE = 60;
  public static final int QUEUE_CAPACITY = Integer.MAX_VALUE;

  public static final boolean ALLOW_THREAD_TIMEOUT = false;

  public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

  private final Map<AbstractModule<?>, NetworkTaskExecutor> taskExecutors;
  private final Map<Integer, ThreadPoolExecutor> priorityExecutors;

  private int currentThreadId;

  public ConcurrentModule(IServer host) {
    super("Concurrent Module", host);

    taskExecutors = new ConcurrentHashMap<>();
    priorityExecutors = new HashMap<>();
  }

  /**
   * Create new {@link ThreadPoolExecutor} with inserted {@link BlockingQueue<Runnable>}. New
   * executor will have the default values that are specified in this class.
   *
   * @param queue {@link BlockingQueue} that will be used to store inserted {@link Runnable}.
   * @return ThreadPoolExecutor
   */
  public ThreadPoolExecutor createSharableThreadPool(BlockingQueue<Runnable> queue) {
    ThreadPoolExecutor executor =
        new ThreadPoolExecutor(
            ConcurrentModule.CORE_POOL_SIZE,
            ConcurrentModule.MAX_POOL_SIZE,
            ConcurrentModule.KEEP_ALIVE,
            ConcurrentModule.DEFAULT_TIMEUNIT,
            queue,
            new DefaultThreadFactory("Thread " + currentThreadId++));

    executor.allowCoreThreadTimeOut(ConcurrentModule.ALLOW_THREAD_TIMEOUT);

    return executor;
  }

  public NetworkTaskExecutor assignTaskExecutor(AbstractModule<?> abstractModule) {
    Module module = abstractModule.getClass().getAnnotation(Module.class);

    if (module == null || !module.asynchronous()) {
      return null;
    }

    int priority = module.priority();
    ThreadPoolExecutor threadPoolExecutor;

    if (priorityExecutors.containsKey(priority)) {
      threadPoolExecutor = priorityExecutors.get(priority);
    } else {
      threadPoolExecutor = createSharableThreadPool(createQueue(ConcurrentModule.QUEUE_CAPACITY));
    }

    Objects.requireNonNull(
        threadPoolExecutor, "Thread pool executor is null. This should not happen.");

    NetworkTaskExecutor taskExecutor = new NetworkTaskExecutor();
    taskExecutor.useThreadPool(threadPoolExecutor);

    priorityExecutors.putIfAbsent(priority, threadPoolExecutor);
    taskExecutors.put(abstractModule, taskExecutor);

    abstractModule.setTaskExecutor(taskExecutor);

    return taskExecutor;
  }

  public BlockingQueue<Runnable> createQueue(int queueCapacity) {
    if (queueCapacity > 0) {
      return new LinkedBlockingQueue<>(queueCapacity);
    }

    return new SynchronousQueue<>();
  }

  public void interrupt() {
    for (NetworkTaskExecutor taskExecutor : taskExecutors.values()) {
      taskExecutor.interrupt();
    }

    for (ThreadPoolExecutor threadPoolExecutor : priorityExecutors.values()) {
      if (!threadPoolExecutor.isTerminated()
          && !threadPoolExecutor.isShutdown()
          && !threadPoolExecutor.isTerminating()) {
        threadPoolExecutor.shutdown();
      }
    }
  }

  public Map<Integer, ThreadPoolExecutor> getPriorityExecutors() {
    return priorityExecutors;
  }

  public Map<AbstractModule<?>, NetworkTaskExecutor> getTaskExecutors() {
    return taskExecutors;
  }

  public int getCurrentThreadId() {
    return currentThreadId;
  }
}
