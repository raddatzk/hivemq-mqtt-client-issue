# Mqtt3 Client stops subscribing on Exception

## Steps to take

1. `docker run -p 8080:8080 -p 1883:1883 --platform linux/amd64 hivemq/hivemq3`
2. start the application from your IDE
3. `mqtt pub -t /topic -m "hello" --mqttVersion 3`

This throws a reactivex UndeliverablException:
```
io.reactivex.exceptions.UndeliverableException: The exception could not be delivered to the consumer because it has already canceled/disposed the flow or the exception has nowhere to go to begin with. Further reading: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling | java.lang.RuntimeException
	at io.reactivex.plugins.RxJavaPlugins.onError(RxJavaPlugins.java:367)
	at io.reactivex.internal.schedulers.ScheduledRunnable.run(ScheduledRunnable.java:69)
	at io.reactivex.internal.schedulers.ScheduledRunnable.call(ScheduledRunnable.java:57)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:833)
Caused by: java.lang.RuntimeException
	at Main.consume(Main.java:24)
	at Main.lambda$main$0(Main.java:19)
	at com.hivemq.client.internal.mqtt.mqtt3.Mqtt3AsyncClientView.lambda$callbackView$1(Mqtt3AsyncClientView.java:76)
	at com.hivemq.client.internal.mqtt.MqttAsyncClient$CallbackSubscriber.onNext(MqttAsyncClient.java:303)
	at com.hivemq.client.internal.mqtt.MqttAsyncClient$CallbackSubscriber.onNext(MqttAsyncClient.java:288)
	at com.hivemq.client.rx.FlowableWithSingle$SingleFutureSubscriber.onNext(FlowableWithSingle.java:406)
	at com.hivemq.client.internal.rx.operators.FlowableWithSingleCombine$SplitSubscriber$Default.tryOnNextActual(FlowableWithSingleCombine.java:235)
	at com.hivemq.client.internal.rx.operators.FlowableWithSingleCombine$SplitSubscriber.tryOnNext(FlowableWithSingleCombine.java:200)
	at io.reactivex.internal.operators.flowable.FlowableObserveOn$ObserveOnConditionalSubscriber.runAsync(FlowableObserveOn.java:649)
	at io.reactivex.internal.operators.flowable.FlowableObserveOn$BaseObserveOnSubscriber.run(FlowableObserveOn.java:176)
	at io.reactivex.internal.schedulers.ScheduledRunnable.run(ScheduledRunnable.java:66)
	... 6 more
Exception in thread "RxComputationThreadPool-1" io.reactivex.exceptions.UndeliverableException: The exception could not be delivered to the consumer because it has already canceled/disposed the flow or the exception has nowhere to go to begin with. Further reading: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling | java.lang.RuntimeException
	at io.reactivex.plugins.RxJavaPlugins.onError(RxJavaPlugins.java:367)
	at io.reactivex.internal.schedulers.ScheduledRunnable.run(ScheduledRunnable.java:69)
	at io.reactivex.internal.schedulers.ScheduledRunnable.call(ScheduledRunnable.java:57)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:833)
Caused by: java.lang.RuntimeException
	at Main.consume(Main.java:24)
	at Main.lambda$main$0(Main.java:19)
	at com.hivemq.client.internal.mqtt.mqtt3.Mqtt3AsyncClientView.lambda$callbackView$1(Mqtt3AsyncClientView.java:76)
	at com.hivemq.client.internal.mqtt.MqttAsyncClient$CallbackSubscriber.onNext(MqttAsyncClient.java:303)
	at com.hivemq.client.internal.mqtt.MqttAsyncClient$CallbackSubscriber.onNext(MqttAsyncClient.java:288)
	at com.hivemq.client.rx.FlowableWithSingle$SingleFutureSubscriber.onNext(FlowableWithSingle.java:406)
	at com.hivemq.client.internal.rx.operators.FlowableWithSingleCombine$SplitSubscriber$Default.tryOnNextActual(FlowableWithSingleCombine.java:235)
	at com.hivemq.client.internal.rx.operators.FlowableWithSingleCombine$SplitSubscriber.tryOnNext(FlowableWithSingleCombine.java:200)
	at io.reactivex.internal.operators.flowable.FlowableObserveOn$ObserveOnConditionalSubscriber.runAsync(FlowableObserveOn.java:649)
	at io.reactivex.internal.operators.flowable.FlowableObserveOn$BaseObserveOnSubscriber.run(FlowableObserveOn.java:176)
	at io.reactivex.internal.schedulers.ScheduledRunnable.run(ScheduledRunnable.java:66)
	... 6 more
```

Sending the same event again will do nothing. 

As a workaround one could catch the exception before it reaches reactive stack
```java
  @RequiredArgsConstructor
  @Slf4j
  static class SlurpingExceptionConsumer implements Consumer<Mqtt3Publish> {

    private final Consumer<Mqtt3Publish> consumer;

    @Override
    public void accept(Mqtt3Publish mqtt3Publish) {
      try {
        consumer.accept(mqtt3Publish);
      } catch (Exception e) {
        log.error("An exception occurred while processing an mqtt event", e);
      }
    }
  }
```