package com.dokidoki.bid.api.service;

import com.dokidoki.bid.api.request.AuctionBidReq;
import com.dokidoki.bid.api.request.AuctionUpdatePriceSizeReq;
import com.dokidoki.bid.api.response.AuctionInitialInfoResp;
import com.dokidoki.bid.api.response.LeaderBoardMemberInfo;
import com.dokidoki.bid.api.response.LeaderBoardMemberResp;
import com.dokidoki.bid.common.codes.LeaderBoardConstants;
import com.dokidoki.bid.common.error.exception.BusinessException;
import com.dokidoki.bid.common.error.exception.ErrorCode;
import com.dokidoki.bid.common.error.exception.InvalidValueException;
import com.dokidoki.bid.db.entity.AuctionIngEntity;
import com.dokidoki.bid.db.entity.AuctionRealtime;
import com.dokidoki.bid.db.entity.UserEntity;
import com.dokidoki.bid.db.repository.AuctionIngRepository;
import com.dokidoki.bid.db.repository.AuctionRealtimeRepository;
import com.dokidoki.bid.db.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"NonAsciiCharacters"})
@DisplayName("LeaderBoardService 클래스")
@ExtendWith(SpringExtension.class)
@SpringBootTest
class BiddingServiceTest {

    @Autowired
    BiddingService biddingService;
    @Autowired AuctionRealtimeRepository auctionRealtimeRepository;
    @Autowired UserRepository userRepository;
    @Autowired AuctionIngRepository auctionIngRepository;
    @Autowired RedisTemplate redisTemplate;

    static long auctionId;
    static String key;
    final static long sellerId = 80_000;
    final static int highestPrice = 7_000_000;
    final static int priceSize = 10_000;
    final static int lifeSpan = 60 * 60 * 24;
    final static long[] memberIds = { 70_000, 70_001};
    final static String[] names = {"사용자0", "사용자1"};
    final static String[] emails = {"user0@gmail.com", "user1@gmail.com"};



    @BeforeEach
    public void 준비() {
        // 기존 DB 내용 삭제
        auctionRealtimeRepository.deleteAll();
        auctionIngRepository.deleteAll();
        userRepository.deleteAll();

        // 사용자 회원가입
        for (int i = 0; i < memberIds.length; i++) {
            UserEntity user = UserEntity.builder()
                    .id(memberIds[i]).name(names[i]).email(emails[i]).build();
            userRepository.save(user);
        }

        UserEntity user = UserEntity.builder()
                .id(sellerId).name("판매자").email("seller@gmail.com").build();

        userRepository.save(user);
        
        // 경매 등록
        AuctionIngEntity auctionIng = AuctionIngEntity.builder()
                .sellerId(sellerId).title("싸트북 팝니다").description("단돈 150만원~")
                .offerPrice(highestPrice).priceSize(priceSize).highestPrice(highestPrice).build();

        auctionIngRepository.save(auctionIng);
        auctionId = auctionIng.getId();
        key = biddingService.getKey(auctionId);

        // 실시간 경매 초기화 정보 등록 (나중엔 메서드로 대체하기)
        AuctionRealtime auctionRealtime = AuctionRealtime.builder()
                .auctionId(auctionId).highestPrice(highestPrice).priceSize(priceSize).lifeSpan(lifeSpan).build();

        auctionRealtimeRepository.save(auctionRealtime);
    }

    @Nested
    @DisplayName("bid 메서드")
    class bid_메서드_테스트 {

        AuctionBidReq[] reqs = new AuctionBidReq[2];

        @BeforeEach
        public void 실시간_경매_초기화() {
            for (int i = 0; i < 2; i ++) {
                reqs[i] = AuctionBidReq.builder()
                        .memberId(memberIds[i])
                        .name(names[i])
                        .email(emails[i])
                        .currentHighestPrice(highestPrice + priceSize * i)
                        .currentPriceSize(priceSize).build();
            }
        }

        @Nested
        @DisplayName("입찰 할 때")
        class 입찰자_제공 {

            AuctionBidReq wrongHighestPriceReq = AuctionBidReq.builder()
                    .memberId(memberIds[0])
                    .name(names[0])
                    .email(emails[0])
                    .currentHighestPrice(highestPrice + 2 * priceSize)
                    .currentPriceSize(priceSize).build();

            AuctionBidReq wrongPriceSizeReq = AuctionBidReq.builder()
                    .memberId(memberIds[0])
                    .name(names[0])
                    .email(emails[0])
                    .currentHighestPrice(highestPrice)
                    .currentPriceSize(priceSize * 2).build();

            @BeforeEach
            public void 준비() {
                AuctionRealtime auctionRealtime = AuctionRealtime.builder()
                        .auctionId(auctionId).highestPrice(highestPrice).priceSize(priceSize).lifeSpan(lifeSpan).build();
                auctionRealtimeRepository.save(auctionRealtime);

                redisTemplate.opsForZSet().removeRange(key, 0, -1);
            }

            @Test
            @DisplayName("없는 경매면 에러를 낸다.")
            public void 입찰실패_없는_경매() {
                assertThrows(InvalidValueException.class, () -> biddingService.bid(auctionId + 2, reqs[0]));
            }

            @Test
            @DisplayName("경매 단위가 일치하지 않으면 Business 에러를 낸다.")
            public void 입찰실패_경매단위_불일치() {
                BusinessException exception = assertThrows(BusinessException.class, () -> {
                    biddingService.bid(auctionId, wrongPriceSizeReq);
                });

                assertEquals(ErrorCode.DIFFERENT_PRICE_SIZE, exception.getErrorCode());

            }

            @Test
            @DisplayName("현재 가격이 일치하지 않으면 Business 에러를 낸다.")
            public void 입찰실패_현재가격_불일치() {
                BusinessException exception = assertThrows(BusinessException.class, () -> {
                    biddingService.bid(auctionId, wrongHighestPriceReq);
                });

                assertEquals(ErrorCode.DIFFERENT_HIGHEST_PRICE, exception.getErrorCode());
            }

            @Test
            @DisplayName("모든 조건을 통과하면 성공적으로 입찰이 된다.")
            public void 입찰성공() {
                biddingService.bid(auctionId, reqs[0]);
                AuctionRealtime auctionRealtime = auctionRealtimeRepository.findById(auctionId).get();

                // auctionRealtime 값 갱신 확인
                assertEquals(highestPrice + priceSize, auctionRealtime.getHighestPrice());

                String key = biddingService.getKey(auctionId);

                // 랭킹 갱신 확인
                AuctionInitialInfoResp initialInfo = biddingService.getInitialInfo(auctionId);
                List<LeaderBoardMemberResp> leaderBoard = initialInfo.getLeaderBoard();
                System.out.println(leaderBoard);
                assertEquals(1, leaderBoard.size());

                Set set = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);

                assertEquals(1, set.size());

                for (Object o : set) {
                    DefaultTypedTuple tuple = (DefaultTypedTuple) o;
                    Double score = tuple.getScore();
                    LeaderBoardMemberInfo memberInfo = (LeaderBoardMemberInfo) tuple.getValue();
                    assertEquals(highestPrice + priceSize, score);
                    assertEquals(reqs[0].getMemberId(), memberInfo.getMemberId());
                    assertEquals(reqs[0].getEmail(), memberInfo.getEmail());
                    assertEquals(reqs[0].getName(), memberInfo.getName());
                }
            }

            @Test
            @DisplayName("새로운 입찰이 일어나면, 가장 위의 정보가 그 사람의 입찰 정보로 갱신된다.")
            public void 입찰성공_사용자_갱신() {
                biddingService.bid(auctionId, reqs[0]);
                biddingService.bid(auctionId, reqs[1]);

                String key = biddingService.getKey(auctionId);

                System.out.println(biddingService.getInitialInfo(auctionId).getLeaderBoard());

                // 랭킹 갱신 확인
                Set<Object> set = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);

                for (Object o : set) {
                    DefaultTypedTuple tuple = (DefaultTypedTuple) o;
                    Double score = tuple.getScore();
                    LeaderBoardMemberInfo memberInfo = (LeaderBoardMemberInfo) tuple.getValue();
                    assertEquals(highestPrice + priceSize * 2, score);
                    assertEquals(memberInfo.getMemberId(), reqs[1].getMemberId());
                    break;
                }
            }

            @Test
            @DisplayName("제한된 수 이상으로 입찰될 경우, 그 개수만큼 최근 입찰된 결과만 sorted Set 에 남는다.")
            public void 제한수_유지() {
                int limit = LeaderBoardConstants.limit;

                for (int i = 0; i < limit + 2; i++) {
                    AuctionBidReq req = AuctionBidReq.builder()
                            .memberId(memberIds[0])
                            .name(names[0])
                            .email(emails[0])
                            .currentHighestPrice(highestPrice + i * priceSize)
                            .currentPriceSize(priceSize).build();
                    biddingService.bid(auctionId, req);
                }

                Set<Object> set = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);

                assertEquals(limit, set.size());
            }
        }
    }
    
    @Nested
    @DisplayName("updatePriceSize 메서드")
    class updatePriceSize_메서드_테스트 {

        @Nested
        @DisplayName("수정할 대상이 주어질 때")
        class 수정할_대상_제공 {

            AuctionUpdatePriceSizeReq correctReq = new AuctionUpdatePriceSizeReq(sellerId, 5_000);
            AuctionUpdatePriceSizeReq wrongReq = new AuctionUpdatePriceSizeReq(sellerId + 30, 5_000);

            @Test
            @DisplayName("없는 경매면 에러를 낸다.")
            public void 입찰단위_수정_실패_없는대상() {
                assertThrows(InvalidValueException.class, ()-> biddingService.updatePriceSize(auctionId + 20, correctReq));
            }

            @Test
            @DisplayName("경매 게시글 작성자가 아니면 에러를 낸다.")
            public void 입찰단위_수정_실패_잘못된_접근() {
                BusinessException exception = assertThrows(BusinessException.class, () -> {
                    biddingService.updatePriceSize(auctionId, wrongReq);
                });
                assertEquals(ErrorCode.BUSINESS_EXCEPTION_ERROR, exception.getErrorCode());
            }
            
            @Test
            @DisplayName("올바르게 접근하면 제대로 수정된다.")
            public void 입찰단위_수정_성공() {
                biddingService.updatePriceSize(auctionId, correctReq);
                AuctionRealtime auctionRealtime = auctionRealtimeRepository.findById(auctionId).get();
                assertEquals(correctReq.getPriceSize(), auctionRealtime.getPriceSize());
            }

        }

    }

}