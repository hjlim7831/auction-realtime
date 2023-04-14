package com.dokidoki.auction.dto.custom;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Slf4j
@Getter
public class SimpleAuctionIngInfo {
    private final Long auction_id;
    private final Long seller_id;
    private final String auction_title;
    private final String category_name;
    private final String product_name;
    private final String meeting_place;

    private final Long offer_price;
    private final Long cur_price;
    private final Long price_size;

    private final LocalDateTime start_time;
    private final LocalDateTime end_time;
    private final Long remain_hours;
    private final Long remain_minutes;
    private final Long remain_seconds;

    private Boolean is_my_interest;
    private Boolean is_my_auction;

    private String auction_image_url;

    @QueryProjection
    public SimpleAuctionIngInfo(Long auctionId, Long sellerId, String auctionTitle, String categoryName, String productName,
                         String meetingPlace, Long offerPrice, Long highestPrice, Long priceSize,
                         LocalDateTime startTime, LocalDateTime endTime) {
        this.auction_id = auctionId;  // 경매 번호
        this.seller_id = sellerId;
        this.auction_title = auctionTitle;  // 경매 제목
        this.category_name = categoryName;  // 분류명
        this.product_name = productName;  // 제품명
        this.meeting_place = meetingPlace;  // 거래 장소
        this.offer_price = offerPrice;  // 시작가
        this.cur_price = highestPrice != null ? highestPrice : offer_price;  // 최고가
        this.price_size = priceSize;  // 경매 단위

        this.start_time = startTime;
        this.end_time = endTime;

        // 남은 시간 계산
        long seconds = 0L;
        try {
            seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), endTime);
        } catch (Exception e) { log.error("SimpleAuctionIngInfo > 끝나는 시간이 존재하지 않습니다."); }

        this.remain_hours = seconds / 3600;
        seconds %= 3600;
        this.remain_minutes = seconds / 60;
        seconds %= 60;
        this.remain_seconds = seconds;
    }

    public void setIsMyInterest(Set<Long> interestsOfUser) {
        this.is_my_interest = interestsOfUser.contains(this.auction_id);
    }
    public void setIsMyAuction(Set<Long> salesOfUser) {
        this.is_my_auction = salesOfUser.contains(this.seller_id);
    }
    public void setImage(String auctionImageUrl) {
        this.auction_image_url = auctionImageUrl;
    }
}
