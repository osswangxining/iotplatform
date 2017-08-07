package org.iotp.server.transport;

import java.util.Optional;

import org.iotp.analytics.ruleengine.common.msg.session.AdaptorToSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.iotp.analytics.ruleengine.common.msg.session.SessionActorToAdaptorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.SessionContext;

public interface TransportAdaptor<C extends SessionContext, T, V> {

  AdaptorToSessionActorMsg convertToActorMsg(C ctx, MsgType type, T inbound) throws AdaptorException;

  Optional<V> convertToAdaptorMsg(C ctx, SessionActorToAdaptorMsg msg) throws AdaptorException;

}
