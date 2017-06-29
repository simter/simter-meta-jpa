package tech.simter.meta;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tech.simter.meta.po.Document;
import tech.simter.meta.po.Operation;
import tech.simter.meta.po.Operator;

import java.time.OffsetDateTime;

/**
 * @author RJ Hwang
 */
public final class POUtils {
  private static int nextCode;
  private static int nextName;

  public static String randomCode() {
    return "c-" + (++nextCode);
  }

  public static String randomName() {
    return "n-" + (++nextName);
  }

  public static void flushAndClear(TestEntityManager em) {
    em.flush();
    em.clear();
  }

  /**
   * Instance one new Operator.
   */
  public static Operator operator(Integer id) {
    Operator po = new Operator();
    po.id = id;
    po.name = randomName();
    return po;
  }

  /**
   * Instance one new Document.
   */
  public static Document document(Class type) {
    Document po = new Document();
    po.type = type.getName();
    return po;
  }

  /**
   * Instance one new Operation.
   */
  public static Operation operation(Operator operator, Operation.Type operateType,
                                    Document document, Integer instanceId) {
    Operation po = new Operation();
    po.instanceId = instanceId;
    po.document = document;
    po.operator = operator;
    po.operateTime = OffsetDateTime.now();
    po.type = operateType.value();
    return po;
  }
}