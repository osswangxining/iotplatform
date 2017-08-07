package org.iotp.server.service.cluster.rpc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.iotp.server.gen.cluster.ClusterAPIProtos;

import io.grpc.stub.StreamObserver;

/**
 */
public class RpcSessionCreationFuture implements Future<StreamObserver<ClusterAPIProtos.ToRpcServerMessage>> {

  private final BlockingQueue<StreamObserver<ClusterAPIProtos.ToRpcServerMessage>> queue = new ArrayBlockingQueue<>(1);

  public void onMsg(StreamObserver<ClusterAPIProtos.ToRpcServerMessage> result) throws InterruptedException {
    queue.put(result);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return false;
  }

  @Override
  public StreamObserver<ClusterAPIProtos.ToRpcServerMessage> get() throws InterruptedException, ExecutionException {
    return this.queue.take();
  }

  @Override
  public StreamObserver<ClusterAPIProtos.ToRpcServerMessage> get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    StreamObserver<ClusterAPIProtos.ToRpcServerMessage> result = this.queue.poll(timeout, unit);
    if (result == null) {
      throw new TimeoutException();
    } else {
      return result;
    }
  }
}
