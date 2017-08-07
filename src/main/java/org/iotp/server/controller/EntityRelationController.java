package org.iotp.server.controller;

import java.util.List;

import org.iotp.infomgt.dao.relation.EntityRelationsQuery;
import org.iotp.infomgt.data.id.EntityId;
import org.iotp.infomgt.data.id.EntityIdFactory;
import org.iotp.infomgt.data.relation.EntityRelation;
import org.iotp.infomgt.data.relation.EntityRelationInfo;
import org.iotp.infomgt.data.relation.RelationTypeGroup;
import org.iotp.server.exception.IoTPErrorCode;
import org.iotp.server.exception.IoTPException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EntityRelationController extends BaseController {

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relation", method = RequestMethod.POST)
  @ResponseStatus(value = HttpStatus.OK)
  public void saveRelation(@RequestBody EntityRelation relation) throws IoTPException {
    try {
      checkNotNull(relation);
      checkEntityId(relation.getFrom());
      checkEntityId(relation.getTo());
      if (relation.getTypeGroup() == null) {
        relation.setTypeGroup(RelationTypeGroup.COMMON);
      }
      relationService.saveRelation(relation).get();
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relation", method = RequestMethod.DELETE, params = { "fromId", "fromType", "relationType",
      "toId", "toType" })
  @ResponseStatus(value = HttpStatus.OK)
  public void deleteRelation(@RequestParam("fromId") String strFromId, @RequestParam("fromType") String strFromType,
      @RequestParam("relationType") String strRelationType,
      @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup,
      @RequestParam("toId") String strToId, @RequestParam("toType") String strToType) throws IoTPException {
    checkParameter("fromId", strFromId);
    checkParameter("fromType", strFromType);
    checkParameter("relationType", strRelationType);
    checkParameter("toId", strToId);
    checkParameter("toType", strToType);
    EntityId fromId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
    EntityId toId = EntityIdFactory.getByTypeAndId(strToType, strToId);
    checkEntityId(fromId);
    checkEntityId(toId);
    RelationTypeGroup relationTypeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
    try {
      Boolean found = relationService.deleteRelation(fromId, toId, strRelationType, relationTypeGroup).get();
      if (!found) {
        throw new IoTPException("Requested item wasn't found!", IoTPErrorCode.ITEM_NOT_FOUND);
      }
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN','TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations", method = RequestMethod.DELETE, params = { "id", "type" })
  @ResponseStatus(value = HttpStatus.OK)
  public void deleteRelations(@RequestParam("entityId") String strId, @RequestParam("entityType") String strType)
      throws IoTPException {
    checkParameter("entityId", strId);
    checkParameter("entityType", strType);
    EntityId entityId = EntityIdFactory.getByTypeAndId(strType, strId);
    checkEntityId(entityId);
    try {
      relationService.deleteEntityRelations(entityId).get();
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relation", method = RequestMethod.GET, params = { "fromId", "fromType", "relationType",
      "toId", "toType" })
  @ResponseBody
  public EntityRelation getRelation(@RequestParam("fromId") String strFromId,
      @RequestParam("fromType") String strFromType, @RequestParam("relationType") String strRelationType,
      @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup,
      @RequestParam("toId") String strToId, @RequestParam("toType") String strToType) throws IoTPException {
    try {
      checkParameter("fromId", strFromId);
      checkParameter("fromType", strFromType);
      checkParameter("relationType", strRelationType);
      checkParameter("toId", strToId);
      checkParameter("toType", strToType);
      EntityId fromId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
      EntityId toId = EntityIdFactory.getByTypeAndId(strToType, strToId);
      checkEntityId(fromId);
      checkEntityId(toId);
      RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
      return checkNotNull(relationService.getRelation(fromId, toId, strRelationType, typeGroup).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations", method = RequestMethod.GET, params = { "fromId", "fromType" })
  @ResponseBody
  public List<EntityRelation> findByFrom(@RequestParam("fromId") String strFromId,
      @RequestParam("fromType") String strFromType,
      @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws IoTPException {
    checkParameter("fromId", strFromId);
    checkParameter("fromType", strFromType);
    EntityId entityId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
    checkEntityId(entityId);
    RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
    try {
      return checkNotNull(relationService.findByFrom(entityId, typeGroup).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations/info", method = RequestMethod.GET, params = { "fromId", "fromType" })
  @ResponseBody
  public List<EntityRelationInfo> findInfoByFrom(@RequestParam("fromId") String strFromId,
      @RequestParam("fromType") String strFromType,
      @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws IoTPException {
    checkParameter("fromId", strFromId);
    checkParameter("fromType", strFromType);
    EntityId entityId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
    checkEntityId(entityId);
    RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
    try {
      return checkNotNull(relationService.findInfoByFrom(entityId, typeGroup).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations", method = RequestMethod.GET, params = { "fromId", "fromType", "relationType" })
  @ResponseBody
  public List<EntityRelation> findByFrom(@RequestParam("fromId") String strFromId,
      @RequestParam("fromType") String strFromType, @RequestParam("relationType") String strRelationType,
      @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws IoTPException {
    checkParameter("fromId", strFromId);
    checkParameter("fromType", strFromType);
    checkParameter("relationType", strRelationType);
    EntityId entityId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
    checkEntityId(entityId);
    RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
    try {
      return checkNotNull(relationService.findByFromAndType(entityId, strRelationType, typeGroup).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations", method = RequestMethod.GET, params = { "toId", "toType" })
  @ResponseBody
  public List<EntityRelation> findByTo(@RequestParam("toId") String strToId, @RequestParam("toType") String strToType,
      @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws IoTPException {
    checkParameter("toId", strToId);
    checkParameter("toType", strToType);
    EntityId entityId = EntityIdFactory.getByTypeAndId(strToType, strToId);
    checkEntityId(entityId);
    RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
    try {
      return checkNotNull(relationService.findByTo(entityId, typeGroup).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations/info", method = RequestMethod.GET, params = { "toId", "toType" })
  @ResponseBody
  public List<EntityRelationInfo> findInfoByTo(@RequestParam("toId") String strToId,
      @RequestParam("toType") String strToType,
      @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws IoTPException {
    checkParameter("toId", strToId);
    checkParameter("toType", strToType);
    EntityId entityId = EntityIdFactory.getByTypeAndId(strToType, strToId);
    checkEntityId(entityId);
    RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
    try {
      return checkNotNull(relationService.findInfoByTo(entityId, typeGroup).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations", method = RequestMethod.GET, params = { "toId", "toType", "relationType" })
  @ResponseBody
  public List<EntityRelation> findByTo(@RequestParam("toId") String strToId, @RequestParam("toType") String strToType,
      @RequestParam("relationType") String strRelationType,
      @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws IoTPException {
    checkParameter("toId", strToId);
    checkParameter("toType", strToType);
    checkParameter("relationType", strRelationType);
    EntityId entityId = EntityIdFactory.getByTypeAndId(strToType, strToId);
    checkEntityId(entityId);
    RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
    try {
      return checkNotNull(relationService.findByToAndType(entityId, strRelationType, typeGroup).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations", method = RequestMethod.POST)
  @ResponseBody
  public List<EntityRelation> findByQuery(@RequestBody EntityRelationsQuery query) throws IoTPException {
    checkNotNull(query);
    checkNotNull(query.getParameters());
    checkNotNull(query.getFilters());
    checkEntityId(query.getParameters().getEntityId());
    try {
      return checkNotNull(relationService.findByQuery(query).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
  @RequestMapping(value = "/relations/info", method = RequestMethod.POST)
  @ResponseBody
  public List<EntityRelationInfo> findInfoByQuery(@RequestBody EntityRelationsQuery query) throws IoTPException {
    checkNotNull(query);
    checkNotNull(query.getParameters());
    checkNotNull(query.getFilters());
    checkEntityId(query.getParameters().getEntityId());
    try {
      return checkNotNull(relationService.findInfoByQuery(query).get());
    } catch (Exception e) {
      throw handleException(e);
    }
  }

  private RelationTypeGroup parseRelationTypeGroup(String strRelationTypeGroup, RelationTypeGroup defaultValue) {
    RelationTypeGroup result = defaultValue;
    if (strRelationTypeGroup != null && strRelationTypeGroup.trim().length() > 0) {
      try {
        result = RelationTypeGroup.valueOf(strRelationTypeGroup);
      } catch (IllegalArgumentException e) {
        result = defaultValue;
      }
    }
    return result;
  }

}
