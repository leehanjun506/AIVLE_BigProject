package swith.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import swith.backend.cond.PostSearchCondition;
import swith.backend.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>{
    //@EntityGraph: fetch join 간편하게 사용할 수 있도록 해주는 어노테이션
    //JPQL: select p from Post p join fetch p.writer w where p.id = :id
    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findWithUserById(Long id);

    @Override
    void delete(Post entity);


//    Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable);
}
