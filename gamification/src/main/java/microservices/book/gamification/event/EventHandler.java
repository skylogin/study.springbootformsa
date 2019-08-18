package microservices.book.gamification.event;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import microservices.book.gamification.service.GameService;

/**
 * 이벤트를 받고 연관된 비지니스 로직을 동작시킴 
 */

@Slf4j
@Component
public class EventHandler {

    private GameService gameService;

    public EventHandler(final GameService gameService){
        this.gameService = gameService;
    }

    @RabbitListener(queues = "${multiplication.queue}")
    public void handleMultiplicationSolved(final MultiplicationSolvedEvent event){
        log.info("Multiplication Solved Event 수신: {}", event.getMultiplicationResultAttemptId());

        try{
            gameService.newAttemptForUser(event.getUserId(), event.getMultiplicationResultAttemptId(), event.isCorrect());
        } catch(final Exception e){
            log.error("MultiplicationSolvedEvent 처리시 에러", e);
            //해당 이벤트가 다시 큐로 들어가거나 두번 처리되지 않도록 예외를 발생시킴
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
    
}