package org.iotp.iothub.server.mqtt;

import io.netty.channel.Channel;

public abstract class ChannelEntity {

	public Channel getChannel() {
		return null;
	}

	public abstract void write(Object message);

	@Override
	public int hashCode() {
		return getChannel().hashCode();
	}

	@Override
	public String toString() {
		return getChannel().toString();
	}
}