package wanted.wantedpreonboardingbackend.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import wanted.wantedpreonboardingbackend.domain.post.dto.PostSearchCondition;
import wanted.wantedpreonboardingbackend.domain.post.entity.Post;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

    @PersistenceContext
    private EntityManager em;

    private long getTotalCount(PostSearchCondition postSearchCondition) {
        String jpql = "select count(p) from Post p where 1=1";
        if (postSearchCondition.getTitle() != null) {
            jpql += " and p.title like :title";
        }
        if (postSearchCondition.getContent() != null) {
            jpql += " and p.content like :content";
        }

        TypedQuery<Long> countQuery = em.createQuery(jpql, Long.class);

        if (postSearchCondition.getTitle() != null) {
            countQuery.setParameter("title", "%" + postSearchCondition.getTitle() + "%");
        }
        if (postSearchCondition.getContent() != null) {
            countQuery.setParameter("content", "%" + postSearchCondition.getContent() + "%");
        }

        return countQuery.getSingleResult();
    }

    @Override
    public Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable) {
        String jpql = "select p from Post p where 1=1";
        if (postSearchCondition.getTitle() != null) {
            jpql += " and p.title like :title";
        }
        if (postSearchCondition.getContent() != null) {
            jpql += " and p.content like :content";
        }

        // Create query
        TypedQuery<Post> query = em.createQuery(jpql, Post.class);

        // Set parameters
        if (postSearchCondition.getTitle() != null) {
            query.setParameter("title", "%" + postSearchCondition.getTitle() + "%");
        }
        if (postSearchCondition.getContent() != null) {
            query.setParameter("content", "%" + postSearchCondition.getContent() + "%");
        }

        // Pagination
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Fetch results and total count
        List<Post> content = query.getResultList();
        long total = getTotalCount(postSearchCondition);

        return new PageImpl<>(content, pageable, total);
    }
}
