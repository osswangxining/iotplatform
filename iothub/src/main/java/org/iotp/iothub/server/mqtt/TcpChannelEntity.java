package org.iotp.iothub.server.mqtt;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class TcpChannelEntity extends ChannelEntity {
	private static ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener() {
		public void operationComplete(ChannelFuture future) {
			if (!future.isSuccess()) {
				future.channel().close();
			}
		}
	};

	protected Channel channel;

	public TcpChannelEntity(Channel channel) {
		this.channel = channel;
	}

	@Override
	public Channel getChannel() {
		return this.channel;
	}

	@Override
	public void write(Object message) {
		if (channel.isOpen())
			this.channel.writeAndFlush(message).addListener(CLOSE_ON_FAILURE);
	}
}