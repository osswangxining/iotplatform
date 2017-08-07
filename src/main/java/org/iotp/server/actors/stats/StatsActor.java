package org.iotp.server.actors.stats;

import org.iotp.analytics.ruleengine.common.msg.cluster.ServerAddress;
import org.iotp.infomgt.data.Event;
import org.iotp.infomgt.data.common.DataConstants;
import org.iotp.server.actors.ActorSystemContext;
import org.iotp.server.actors.service.ContextAwareActor;
import org.iotp.server.actors.service.ContextBasedCreator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class StatsActor extends ContextAwareActor {

  private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
  private final ObjectMapper mapper = new ObjectMapper();

  public StatsActor(ActorSystemContext context) {
    super(context);
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    logger.debug("Received message: {}", msg);
    if (msg instanceof StatsPersistMsg) {
      try {
        onStatsPersistMsg((StatsPersistMsg) msg);
      } catch (Exception e) {
        logger.warning("Failed to persist statistics: {}", msg, e);
      }
    }
  }

  public void onStatsPersistMsg(StatsPersistMsg msg) throws Exception {
    Event event = new Event();
    event.setEntityId(msg.getEntityId());
    event.setTenantId(msg.getTenantId());
    event.setType(DataConstants.STATS);
    event.setBody(toBodyJson(systemContext.getDiscoveryService().getCurrentServer().getServerAddress(),
        msg.getMessagesProcessed(), msg.getErrorsOccurred()));
    systemContext.getEventService().save(event);
  }

  private JsonNode toBodyJson(ServerAddress server, long messagesProcessed, long errorsOccurred) {
    return mapper.createObjectNode().put("server", server.toString()).put("messagesProcessed", messagesProcessed)
        .put("errorsOccurred", errorsOccurred);
  }

  public static class ActorCreator extends ContextBasedCreator<StatsActor> {
    private static final long serialVersionUID = 1L;

    public ActorCreator(ActorSystemContext context) {
      super(context);
    }

    @Override
    public StatsActor create() throws Exception {
      return new StatsActor(context);
    }
  }
}
