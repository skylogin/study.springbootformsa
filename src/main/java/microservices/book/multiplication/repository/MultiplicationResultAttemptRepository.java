package microservices.book.multiplication.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import microservices.book.multiplication.domain.MultiplicationResultAttempt;

/**
 * {@link MultiplicationResultAttempt} 답안을 저장하고 조회하기 위한 인터페이스
 */
public interface MultiplicationResultAttemptRepository extends CrudRepository<MultiplicationResultAttempt, Long> {

  /**
   * @return 닉네임에 해당하는 사용자의 최근 답안 5개
   */
  public List<MultiplicationResultAttempt> findTop5ByUserAliasOrderByIdDesc(String userAlias);

  /**
   * 
   * @return 닉네임에 해당하는 사용자가 풀었던 문제인지 확인
   */
  public List<MultiplicationResultAttempt> findByUserAliasAndMultiplicationFactorAAndMultiplicationFactorB(
      String userAlias, int factorA, int factorB);
}