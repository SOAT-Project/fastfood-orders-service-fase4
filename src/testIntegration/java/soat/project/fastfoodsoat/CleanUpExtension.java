package soat.project.fastfoodsoat;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.*;

import java.util.Collection;
import java.util.List;

public class CleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final var appContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                appContext.getBean(ClientRepository.class),
                appContext.getBean(OrderRepository.class),
                appContext.getBean(ProductRepository.class),
                appContext.getBean(ProductCategoryRepository.class),
                appContext.getBean(OrderProductRepository.class),
                appContext.getBean(OrderRepository.class)
        ));
    }

    private void cleanUp(final Collection<CrudRepository<?, ?>> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }

}
