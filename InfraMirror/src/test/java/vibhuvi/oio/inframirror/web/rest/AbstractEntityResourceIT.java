package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Abstract base class for entity integration tests.
 * Provides common test utilities and helper methods.
 *
 * @param <E> Entity type
 * @param <R> Repository type
 */
public abstract class AbstractEntityResourceIT<E, R extends JpaRepository<E, Long>> {

    @Autowired
    protected ObjectMapper om;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected MockMvc restMockMvc;

    /**
     * Get the repository for this entity.
     */
    protected abstract R getRepository();

    /**
     * Get the API URL for this entity (e.g., "/api/regions").
     */
    protected abstract String getEntityApiUrl();

    /**
     * Flush and clear entity manager to trigger database operations.
     */
    protected void flushAndClear() {
        em.flush();
        em.clear();
    }

    /**
     * Get current repository count.
     */
    protected long getRepositoryCount() {
        return getRepository().count();
    }

    /**
     * Assert repository count increased by 1.
     */
    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    /**
     * Assert repository count decreased by 1.
     */
    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    /**
     * Assert repository count unchanged.
     */
    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    /**
     * Get persisted entity by ID.
     */
    protected E getPersistedEntity(Long id) {
        return getRepository().findById(id).orElseThrow();
    }
}
