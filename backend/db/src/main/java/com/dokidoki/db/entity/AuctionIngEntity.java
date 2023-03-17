package com.dokidoki.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auction_ing")
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AuctionIngEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = MemberEntity.class)
    @JoinColumn(name = "seller_id")
    private MemberEntity member;

    @Column(name = "seller_id", insertable = false, updatable = false)
    private Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ProductEntity.class)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "product_id", insertable = false, updatable = false)
    private Long productId;

    private String title;               // 제목

    private String description;         // 제품설명

    @Column(name = "offer_price")
    private Integer offerPrice;             // 시작 가격(호가)

    @Column(name = "price_size")
    private Integer priceSize;              // 경매 단위

    @Column(name = "end_at")
    private LocalDateTime endAt;        // 경매 종료 시점

    @Column(name = "meeting_place")
    private String meetingPlace;        // 거래장소

    @Column(name = "highest_price")
    private Integer highestPrice;           // 현재 최고가

    @OneToMany(mappedBy = "auctionIng", cascade = CascadeType.ALL)
    private List<LeaderBoardEntity> leaderBoardEntities = new ArrayList<>();
}