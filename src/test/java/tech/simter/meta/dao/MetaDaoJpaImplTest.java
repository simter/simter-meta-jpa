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
import tech.simter.meta.po.Document;
import tech.simter.meta.po.Operation;
import tech.simter.meta.po.Operator;

import java.time.OffsetDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MetaDaoJpaImpl.class})
@DataJpaTest
@EntityScan(basePackageClasses = {Operation.class})
public class MetaDaoJpaImplTest {
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private MetaDao metaDao;

  @Before
  public void setUp() throws Exception {
    entityManager.getEntityManager().createQuery("delete from Operation").executeUpdate();
    entityManager.getEntityManager().createQuery("delete from Operator").executeUpdate();
    entityManager.getEntityManager().createQuery("delete from Document").executeUpdate();
    entityManager.flush();
  }

  @Test
  public void getDocumentByClass() throws Exception {
    assertThat(metaDao.getDocument(MyDoc.class), nullValue());
    Document po = new Document();
    po.type = MyDoc.class.getName();
    entityManager.persist(po);
    entityManager.flush();
    entityManager.clear();

    assertThat(metaDao.getDocument(MyDoc.class).id, is(po.id));
  }

  @Test
  public void getDocumentByString() throws Exception {
    assertThat(metaDao.getDocument(MyDoc.class.getName()), nullValue());
    Document po = new Document();
    po.type = MyDoc.class.getName();
    entityManager.persist(po);
    entityManager.flush();
    entityManager.clear();

    assertThat(metaDao.getDocument(MyDoc.class.getName()).id, is(po.id));
  }

  @Test
  public void createDocument() throws Exception {
    Document po = new Document();
    po.type = MyDoc.class.getName();
    metaDao.createDocument(po);
    entityManager.flush();
    entityManager.clear();

    assertThat(metaDao.getDocument(MyDoc.class).id, is(po.id));
  }

  @Test
  public void getOperator() throws Exception {
    assertThat(metaDao.getOperator(1), nullValue());
    Operator po = new Operator();
    po.id = 1;
    po.name = "simter";
    entityManager.persist(po);
    entityManager.flush();
    entityManager.clear();

    assertThat(metaDao.getOperator(1).id, is(po.id));
  }

  @Test
  public void createOperator() throws Exception {
    Operator po = new Operator();
    po.id = 1;
    po.name = "simter";
    metaDao.createOperator(po);
    entityManager.flush();
    entityManager.clear();

    assertThat(metaDao.getOperator(1).id, is(po.id));
  }

  @Test
  public void createOperation() throws Exception {
    Operator operator = new Operator();
    operator.id = 1;
    operator.name = "simter";
    entityManager.persist(operator);

    Document doc = new Document();
    doc.type = MyDoc.class.getName();
    entityManager.persist(doc);

    Operation history = new Operation();
    history.operateTime = OffsetDateTime.now();
    history.operator = operator;
    history.type = Operation.Type.Creation.value();
    history.document = doc;
    history.instanceId = 1;
    metaDao.createOperation(history);
    entityManager.flush();
    entityManager.clear();

    assertThat(entityManager.find(Operation.class, history.id).id, is(history.id));
  }

  class MyDoc {
  }
}