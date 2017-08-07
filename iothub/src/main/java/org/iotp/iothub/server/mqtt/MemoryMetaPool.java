package org.iotp.iothub.server.mqtt;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class MemoryMetaPool {

  private final static ConcurrentHashMap<String, ChannelEntity> cientIdChannelMap = new ConcurrentHashMap<String, ChannelEntity>(
      1000000, 0.9f, 256);
  private final static ConcurrentHashMap<Channel, String> channelClientIdMap = new ConcurrentHashMap<Channel, String>();

  private final static ConcurrentHashMap<String, Set<ChannelEntity>> topicChannelMap = new ConcurrentHashMap<String, Set<ChannelEntity>>(
      1000000, 0.9f, 256);

  private final static ConcurrentHashMap<ChannelEntity, Set<String>> channelTopicMap = new ConcurrentHashMap<ChannelEntity, Set<String>>();

  private final static ChannelFutureListener clientRemover = new ChannelFutureListener() {
    public void operationComplete(ChannelFuture future) throws Exception {
      removeChannel(future.channel());
    }
  };

  public static void registerClienId(String clientId, ChannelEntity chn) {
    if (chn == null) {
      return;
    }
    if (clientId == null) {
      return;
    }

    ChannelEntity oldChannel = cientIdChannelMap.put(clientId, chn);
    if (oldChannel != null) {
      unregisterChannel(oldChannel);
    }
  }

  public static void unregisterClientId(String clientId) {
    if (clientId == null) {
      return;
    }

    cientIdChannelMap.remove(clientId);
  }

  public static void registerClienId(String clientId, Channel chn) {
    if (chn == null) {
      return;
    }
    if (clientId == null) {
      return;
    }
    chn.closeFuture().addListener(clientRemover);
    channelClientIdMap.put(chn, clientId);
    ChannelEntity oldChannel = cientIdChannelMap.put(clientId, new TcpChannelEntity(chn));
    if (oldChannel != null) {
      removeChannel(oldChannel.getChannel());
      oldChannel.getChannel().close();
    }
  }

  public static void removeChannel(Channel chn) {
    Set<String> topicSet = channelTopicMap.remove(chn);
    if (topicSet != null) {
      for (String topic : topicSet) {
        unregisterTopic(chn, topic);
      }
    }

    String clientId = channelClientIdMap.remove(chn);
    if (clientId != null) {
      cientIdChannelMap.remove(clientId, chn);
    }

    chn.closeFuture().removeListener(clientRemover);
  }

  public static void unregisterChannel(ChannelEntity chn) {
    Set<String> topicSet = channelTopicMap.remove(chn);
    if (topicSet != null) {
      for (String topic : topicSet) {
        unregisterTopic(chn, topic);
      }
    }

    if (chn.getChannel() != null) {
      String clientId = channelClientIdMap.remove(chn.getChannel());
      if (clientId != null) {
        cientIdChannelMap.remove(clientId, chn);
      }

      chn.getChannel().closeFuture().removeListener(clientRemover);
    }
  }

  public static void registerTopic(ChannelEntity chn, String topic) {
    if (chn == null) {
      return;
    }
    if (topic == null) {
      return;
    }

    Set<String> topicSet = channelTopicMap.get(chn);
    if (topicSet == null) {
      topicSet = new HashSet<String>(1);
    }
    topicSet.add(topic);

    channelTopicMap.put(chn, topicSet);

    Set<ChannelEntity> channelSet = topicChannelMap.get(topic);
    if (channelSet == null) {
      channelSet = new HashSet<ChannelEntity>(1);
    }
    channelSet.add(chn);

    topicChannelMap.put(topic, channelSet);
  }

  public static void unregisterTopic(Channel chn, String topic) {
    Set<ChannelEntity> channelSet = topicChannelMap.get(topic);
    for (ChannelEntity oneChn : channelSet) {
      if (oneChn.getChannel() == chn) {
        channelSet.remove(oneChn);
        break;
      }
    }
    channelSet.remove(chn);
    if (channelSet.isEmpty()) {
      topicChannelMap.remove(topic);
    }
  }

  public static void unregisterTopic(ChannelEntity chn, String topic) {
    Set<ChannelEntity> channelSet = topicChannelMap.get(topic);
    channelSet.remove(chn);
    if (channelSet.isEmpty()) {
      topicChannelMap.remove(topic);
    }
  }

  public static String getClientId(Channel chn) {
    return channelClientIdMap.get(chn);
  }

  public static ChannelEntity getChannelEntryByClientId(String clientID) {
    return cientIdChannelMap.get(clientID);
  }

  public static boolean checkClientID(String clientID) {
    return cientIdChannelMap.containsKey(clientID);
  }

  public static Set<ChannelEntity> getChannelByTopics(String topic) {
    if (topic == null) {
      return null;
    }
    return topicChannelMap.get(topic);
  }

  public static Set<String> getTopicsByChannelEntry(ChannelEntity chn) {
    return channelTopicMap.get(chn);
  }
}