package tech.simter.meta.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tech.simter.meta.po.MetaDoc;
import tech.simter.meta.po.MetaHistory;
import tech.simter.meta.po.MetaType;

import java.time.OffsetDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MetaDaoJpaImpl.class})
@DataJpaTest
@EntityScan(basePackageClasses = {MetaHistory.class})
public class MetaDaoJpaImplTest {
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private MetaDao metaDao;

  @Before
  public void setUp() throws Exception {
    entityManager.getEntityManager().createQuery("delete from MetaType").executeUpdate();
    entityManager.getEntityManager().createQuery("delete from MetaDoc").executeUpdate();
    entityManager.getEntityManager().createQuery("delete from MetaHistory").executeUpdate();
    entityManager.flush();
  }

  @Test
  public void getMetaType() throws Exception {
    assertThat(metaDao.getMetaType("creation"), nullValue());
    MetaType po = new MetaType();
    po.type = "creation";
    entityManager.persist(po);
    assertThat(metaDao.getMetaType("creation"), is(po));
  }

  @Test
  public void createMetaType() throws Exception {
    MetaType po = new MetaType();
    po.type = "xxx";
    metaDao.createMetaType(po);
    assertThat(metaDao.getMetaType("xxx"), is(po));
  }

  @Test
  public void getMetaDoc() throws Exception {
    assertThat(metaDao.getMetaDoc(MyDoc.class), nullValue());
    MetaDoc po = new MetaDoc();
    po.type = MyDoc.class.getName();
    entityManager.persist(po);
    assertThat(metaDao.getMetaDoc(MyDoc.class), is(po));
  }

  @Test
  public void createMetaDoc() throws Exception {
    MetaDoc po = new MetaDoc();
    po.type = MyDoc.class.getName();
    metaDao.createMetaDoc(po);
    assertThat(metaDao.getMetaDoc(MyDoc.class), is(po));
  }

  @Test
  public void createMetaHistory() throws Exception {
    MetaType metaType = new MetaType();
    metaType.type = "xxx";
    entityManager.persist(metaType);

    MetaDoc metaDoc = new MetaDoc();
    metaDoc.type = MyDoc.class.getName();
    entityManager.persist(metaDoc);

    MetaHistory metaHistory = new MetaHistory();
    metaHistory.time = OffsetDateTime.now();
    metaHistory.actor = 1;
    metaHistory.metaType = metaType;
    metaHistory.metaDoc = metaDoc;
    metaHistory.docId = 1;
    metaDao.createMetaHistory(metaHistory);

    assertThat(entityManager.find(MetaHistory.class, metaHistory.id), is(metaHistory));
  }

  class MyDoc {
  }
}