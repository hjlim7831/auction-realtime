package com.dokidoki.auction.domain.custom;

import com.dokidoki.auction.domain.entity.AuctionIngEntity;
import com.dokidoki.auction.domain.entity.QAuctionIngEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.dokidoki.auction.domain.entity.QAuctionIngEntity.*;

@RequiredArgsConstructor
public class AuctionIngCustomImpl implements AuctionIngCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AuctionIngEntity> searchAuctionIng(Pageable pageable) {
        List<AuctionIngEntity> result = queryFactory
                .select

        return null;
    }
}
