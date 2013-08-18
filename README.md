orderquery
==========

Framework for dynamic orderable JPQL queries
The goal of this project is to develop a framework which allows to write dynamic orderable JPQL queries in the same fashion as NamedQueries. But keep in mind that these queries are not pre compiled.

The framework itself is at the moment under development so don't expect a bug free implementation. The configuration mentioned in the usage section should fully work. The testing needs a lot of time because I have to write tests for different applications servers to ensure compatibility. The current version is only tested with Glassfish 3.1.2.

## Usage
Its required to but a xml file with name orderquery.xml in the META-INF folder. This an example from a simple test environment. Further description follow later.

    <?xml version="1.0" encoding="UTF-8"?>
    <orderqueries basePackage="mh.dev.common.orderquery.test.environment.model">
        <model name="xmlModel" type="mh.dev.common.orderquery.test.environment.model.XmlModel">
            <column name="field1" query="xm.field1" />
            <column name="field2" query="xm.field2" />
        </model>
        <model name="mixedModel" type="mh.dev.common.orderquery.test.environment.model.MixedModel">
            <column name="field1" query="mm.field1" />
            <column name="field2" query="mm.field2" />
        </model>
        <orderquery name="xmlModel" model="xmlModel">
            <query>
                SELECT xm FROM XmlModel xm
            </query>
        </orderquery>
    </orderqueries>

An example for annotation bases configuration is:

	@Entity
	@OrderQueryModel
	@OrderQueries({ @OrderQuery(name = "annotationModel", query = "Select am From AnnotationModel am") })
	public class AnnotationModel {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;

		@OrderQueryColumn(query = "am.field1")
		private String field1;

		@OrderQueryColumn(query = "am.field2")
		private String field2;
	...
	}

The current version allows only to use ordered queries if the order state is given by the OrderState object which can be injected with the name of the query.

    public class AnnotationModelOrderStateTests extends TestCore {

        @EJB
        private AnnotationModelService service;

        @Inject
        @OrderStateConfig(queryName = "annotationModel")
        private OrderState orderState;

        ...    
    }

The rendering of the ordered query work via the render method of the OrderQueryBuild which expectes the OrderState as argument. The OrderQueryBuilder itself can be injected too.

    public abstract class Repository<E> {

        @PersistenceContext
        private EntityManager em;

        @Inject
        private OrderQueryBuilder orderQueryBuilder;

        @SuppressWarnings("unchecked")
        public List<E> all(OrderState orderState) {
            Query query = em.createQuery(orderQueryBuilder.render(orderState));
            return query.getResultList();
        }
    }
