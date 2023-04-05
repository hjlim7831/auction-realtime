package com.dokidoki.auction.domain.entity;

import com.dokidoki.auction.dto.request.AuctionUpdateReq;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table(name = "auction_ing")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AuctionIngEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private MemberEntity seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    private String title;               // 제목

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;         // 제품설명

    @Column(name = "offer_price")
    private Long offerPrice;             // 시작 가격(호가)

    @Column(name = "price_size")
    private Long priceSize;              // 경매 단위

    @CreatedDate
    @Column(name = "start_time")
    private LocalDateTime startTime;    // 경매 시작 시간

    @Column(name = "end_at")            // 판매자가 정의한
    private LocalDateTime endAt;        // 경매 종료 시점

    @Column(name = "meeting_place")
    private String meetingPlace;        // 거래장소

    @Column(name = "highest_price")
    private Long highestPrice;           // 현재 최고가

    @Builder
    public AuctionIngEntity(Long id, MemberEntity seller, ProductEntity productEntity, String title, String description,
                            Long offerPrice, Long priceSize, LocalDateTime startTime, LocalDateTime endAt,
                            String meetingPlace, Long highestPrice) {
        this.id = id;
        this.seller = seller;
        this.productEntity = productEntity;
        this.title = title;
        this.description = description;
        this.offerPrice = offerPrice;
        this.priceSize = priceSize;
        this.startTime = startTime;
        this.endAt = endAt;
        this.meetingPlace = meetingPlace;
        this.highestPrice = highestPrice;
    }

    public void update(AuctionUpdateReq auctionUpdateReq) {
        this.title = auctionUpdateReq.getTitle();
        this.description = auctionUpdateReq.getDescription();
        this.priceSize = auctionUpdateReq.getPriceSize();
        this.meetingPlace = auctionUpdateReq.getMeetingPlace();
    }
}
