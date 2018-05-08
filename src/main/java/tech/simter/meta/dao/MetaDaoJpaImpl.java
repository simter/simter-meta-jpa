package tech.simter.meta.dao;

import tech.simter.meta.po.Document;
import tech.simter.meta.po.Operation;
import tech.simter.meta.po.Operator;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * The MetaDAO JPA Implementation.
 *
 * @author RJ
 */
@Named
@Singleton
public class MetaDaoJpaImpl implements MetaDao {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Document getDocument(String type) {
    try {
      return entityManager.createQuery("select d from Document d where type = :type", Document.class)
        .setParameter("type", type)
        .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public Document getDocument(Class type) {
    return null == type ? null : getDocument(type.getName());
  }

  @Override
  public void createDocument(Document document) {
    entityManager.persist(document);
  }

  @Override
  public Operator getOperator(Integer operatorId) {
    try {
      return entityManager.createQuery("select u from Operator u where id = :id", Operator.class)
        .setParameter("id", operatorId)
        .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public void createOperator(Operator operator) {
    entityManager.persist(operator);
  }

  @Override
  public void createOperation(Operation operation) {
    entityManager.persist(operation);
  }

  @Override
  public Document createOrGetDocumentByType(String type, String name) {
    Document document = getDocument(type);
    if (document == null) {
      document = new Document();
      document.type = type;
      document.name = name == null || name.isEmpty() ? type : name;
      createDocument(document);
    }
    return document;
  }

  @Override
  public Operator getCreator(Class entityType, Integer entityId) {
    try {
      String ql = "select o.operator from Operation o" +
        " where o.document.type = :entityType and o.instanceId = :entityId and o.type = :type";
      return entityManager.createQuery(ql, Operator.class)
        .setParameter("entityType", entityType.getName())
        .setParameter("entityId", entityId)
        .setParameter("type", Operation.Type.Creation.value())
        .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public Operation getLastOperation(String entityType, Integer entityId, int operationType) {
    return getLastOperation(entityType, entityId, new int[]{operationType});
  }

  @Override
  public Operation getLastOperation(String entityType, Integer entityId, int[] operationTypes) {
    boolean hasOperationType = operationTypes != null && operationTypes.length > 0;
    try {
      String ql = "select o from Operation o" +
        " where o.document.type = :entityType and o.instanceId = :entityId";
      if (hasOperationType) ql += " and o.type in :types";
      ql += " order by o.operateOn desc, o.id desc";
//      ql += "\nand not exists (" +
//        "\n  select 0 from Operation o2" +
//        "\n  where o2.document.type = o.document.type and o2.instanceId = o.instanceId";
//      if (hasOperationType) ql += " and o2.type in :types";
//      ql += "\n  and o2.operateOn < o.operateOn" +
//        "\n)";
      TypedQuery<Operation> query = entityManager.createQuery(ql, Operation.class)
        .setParameter("entityType", entityType)
        .setParameter("entityId", entityId);
      if (hasOperationType) {
        List<Integer> types = new ArrayList<>();
        for (int i = 0; i < operationTypes.length; i++) types.add(operationTypes[i]);
        query.setParameter("types", types);
      }
      query.setFirstResult(0);
      query.setMaxResults(1);
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
}