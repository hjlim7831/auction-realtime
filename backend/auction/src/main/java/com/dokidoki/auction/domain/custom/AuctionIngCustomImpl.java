package com.dokidoki.auction.domain.custom;

import com.dokidoki.auction.dto.custom.QSimpleAuctionIngInfo;
import com.dokidoki.auction.dto.custom.SimpleAuctionIngInfo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.dokidoki.auction.domain.entity.QAuctionIngEntity.*;
import static com.dokidoki.auction.domain.entity.QProductEntity.*;
import static com.dokidoki.auction.domain.entity.QMemberEntity.*;
import static com.dokidoki.auction.domain.entity.QCategoryEntity.*;

@RequiredArgsConstructor
public class AuctionIngCustomImpl implements AuctionIngCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SimpleAuctionIngInfo> searchAuctionIng(String keyword, Long categoryId, LocalDateTime afterNHours,
                                                       Long sellerId, Long[] biddingAuctionList, Pageable pageable) {
        List<SimpleAuctionIngInfo> result = queryFactory
                .select(new QSimpleAuctionIngInfo(
                        auctionIngEntity.id,
                        memberEntity.id,
                        auctionIngEntity.title,
                        categoryEntity.categoryName,
                        productEntity.name,
                        auctionIngEntity.meetingPlace,
                        auctionIngEntity.offerPrice,
                        auctionIngEntity.highestPrice,
                        auctionIngEntity.priceSize,
                        auctionIngEntity.startTime,
                        auctionIngEntity.endAt
                ))
                .from(auctionIngEntity)
                .innerJoin(auctionIngEntity.seller, memberEntity).fetchJoin()
                .innerJoin(auctionIngEntity.productEntity, productEntity).fetchJoin()
                .innerJoin(productEntity.categoryEntity, categoryEntity).fetchJoin()
                .where(
                        eqKeyword(keyword),
                        eqCategoryId(categoryId),
                        withinTime(afterNHours),
                        eqSellerId(sellerId),
                        inBiddingAuctionList(biddingAuctionList)
                )
                .orderBy(auctionIngEntity.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auctionIngEntity.count())
                .from(auctionIngEntity)
                .where(
                        eqKeyword(keyword),
                        eqCategoryId(categoryId)
                );

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    BooleanExpression eqCategoryId(Long categoryId) {
        if (categoryId == null)
            return null;
        if (1 <= categoryId && categoryId <= 8) {
            try {
                return auctionIngEntity
                        .productEntity
                        .categoryEntity
                        .id.eq(categoryId);
            } catch (Exception ignored) {}
        }
        return null;
    }

    BooleanExpression eqKeyword(String keyword) {
        if (keyword == null || keyword.isBlank())
            return null;
        return auctionIngEntity
                .title.contains(keyword)
                .or(auctionIngEntity.productEntity.categoryEntity.categoryName.contains(keyword))
                .or(auctionIngEntity.productEntity.name.contains(keyword));
    }

    BooleanExpression withinTime(LocalDateTime afterNHours) {
        if (afterNHours == null)
            return null;
        return auctionIngEntity.endAt.eq(afterNHours)
                .or(auctionIngEntity.endAt.before(afterNHours));
    }

    BooleanExpression eqSellerId(Long sellerId) {
        if (sellerId == null || auctionIngEntity.seller == null)
            return null;
        return auctionIngEntity.seller.id.eq(sellerId);
    }

    BooleanExpression inBiddingAuctionList(Long[] biddingAuctionList) {
        if (biddingAuctionList == null)
            return null;
        return auctionIngEntity.id.in(biddingAuctionList);
    }
}
