package com.dokidoki.userserver.controller;

import com.dokidoki.userserver.componet.JwtProvider;
import com.dokidoki.userserver.exception.CustomException;
import com.dokidoki.userserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteMember(@NotNull @PathVariable(name = "user_id") Long userId,
                                          HttpServletRequest request){
        Long myId = jwtProvider.getUserId(request);

        if(myId != userId) throw new CustomException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        userService.deleteMember(userId);
        return ResponseEntity.noContent().build();
    }
}
