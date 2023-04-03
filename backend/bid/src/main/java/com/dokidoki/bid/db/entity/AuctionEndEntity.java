package com.dokidoki.bid.db.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_end")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionEndEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private MemberEntity seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private MemberEntity buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "start_time")
    private LocalDateTime startTime;        // 경매 시작 시간

    @Column(name = "end_time")
    private LocalDateTime endTime;        // 경매 종료 시간

    private String title;               // 제목

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;         // 제품설명

    @Column(name = "offer_price")
    private Long offerPrice;         // 시작가

    @Column(name = "final_price")
    private Long finalPrice;         // 낙찰금액

    public static AuctionEndEntity createAuctionEnd(
            Long id,
            MemberEntity seller,
            MemberEntity buyer,
            ProductEntity product,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String title,
            Long offerPrice,
            Long finalPrice
    ) {
        AuctionEndEntity auctionEndEntity = new AuctionEndEntity();
        auctionEndEntity.id = id;
        auctionEndEntity.seller = seller;
        auctionEndEntity.buyer = buyer;
        auctionEndEntity.product = product;
        auctionEndEntity.startTime = startTime;
        auctionEndEntity.endTime = endTime;
        auctionEndEntity.title = title;
        auctionEndEntity.offerPrice = offerPrice;
        auctionEndEntity.finalPrice = finalPrice;
        return auctionEndEntity;
    }
}
