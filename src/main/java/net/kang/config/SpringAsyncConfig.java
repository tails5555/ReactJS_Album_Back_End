package net.kang.config;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class SpringAsyncConfig {
	// 비동기 주체를 가진 Bean에 대해서 각 동작들에 대해 인터럽트 발생을 Log를 이용해서 확인을 한다.
	protected Logger logger = LoggerFactory.getLogger(getClass());
    protected Logger errorLogger = LoggerFactory.getLogger("error");

    // 이 비동기 주체는 사용자가 파일을 업로드할 때 비동기로 받아서 Executor01, Executor02, Executor03 대로 순회를 돌면서 실행을 한다.
	@Bean(name="photoFileUploadExecutor")
	public Executor photoFileUploadExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(30);
		taskExecutor.setQueueCapacity(10);
		taskExecutor.setThreadNamePrefix("UploadExecutor-");
		taskExecutor.initialize();
		return new HandlingExecutor(taskExecutor);
	}

	// 이 비동기 주체는 사용자가 파일을 삭제할 때 비동기로 받아서 Executor01, Executor02, Executor03 대로 순회를 돌면서 실행을 한다.
	@Bean(name="photoDeleteExecutor")
	public Executor photoDeleteExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(30);
		taskExecutor.setQueueCapacity(10);
		taskExecutor.setThreadNamePrefix("DeleteExecutor-");
		taskExecutor.initialize();
		return new HandlingExecutor(taskExecutor);
	}

	// HandlingExecutor 클래스는 Async(비동기)의 주체가 동작을 할 때 필요한 Executor의 골격체의 일부 구현인데 이는 추후에 필요한 조건에 맞춰서 작성을 할 필요가 있다.
	public class HandlingExecutor implements AsyncTaskExecutor{
		private AsyncTaskExecutor executor;
		public HandlingExecutor(AsyncTaskExecutor executor) {
			this.executor=executor;
		}

		@Override
		public void execute(Runnable task) {
			executor.execute(task);
		}

		@Override
        public void execute(Runnable task, long startTimeout) {
            executor.execute(createWrappedRunnable(task), startTimeout);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return executor.submit(createWrappedRunnable(task));
        }

        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            return executor.submit(createCallable(task));
        }

        private <T> Callable<T> createCallable(final Callable<T> task) {
            return new Callable<T>() {
                @Override
                public T call() throws Exception {
                    try {
                        return task.call();
                    } catch (Exception ex) {
                        handle(ex);
                        throw ex;
                    }
                }
            };
        }

        private Runnable createWrappedRunnable(final Runnable task) {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        task.run();
                    } catch (Exception ex) {
                        handle(ex);
                    }
                }
            };
        }

        private void handle(Exception ex) {
            errorLogger.info("Failed to execute task. : {}", ex.getMessage());
            errorLogger.error("Failed to execute task. ",ex);
        }
	}
}
