package microservices.book.gamification.service;

import java.util.List;

import microservices.book.gamification.domain.LeaderBoardRow;

/**
 * LeaderBoard에 접근하는 메서드를 제공
 */
public interface LeaderBoardService {

    /**
     * 최고 점수 사용자와 함께 현재 리더보드를 검색
     * @return 최고 점수와 사용자
     */
    public List<LeaderBoardRow> getCurrentLeaderBoard();
}