package tech.simter.meta.dao;

import tech.simter.meta.po.MetaDoc;
import tech.simter.meta.po.MetaHistory;
import tech.simter.meta.po.MetaType;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * JPA DAO Implementation
 *
 * @author RJ 2017-04-25
 */
@Named
@Singleton
public class MetaDaoJpaImpl implements MetaDao {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void createMetaType(MetaType metaType) {
    entityManager.persist(metaType);
  }

  @Override
  public void createMetaDoc(MetaDoc metaDoc) {
    entityManager.persist(metaDoc);
  }

  @Override
  public void createMetaHistory(MetaHistory metaHistory) {
    entityManager.persist(metaHistory);
  }

  @Override
  public MetaType getMetaType(String metaType) {
    try {
      return entityManager.createQuery("select m from MetaType m where type = :type", MetaType.class)
        .setParameter("type", metaType)
        .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public MetaDoc getMetaDoc(Class docType) {
    try {
      return entityManager.createQuery("select m from MetaDoc m where type = :type", MetaDoc.class)
        .setParameter("type", docType.getName())
        .getSingleResult();
    } catch (Exception e) {
      return null;
    }
  }
}