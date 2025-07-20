package com.playblog.blogservice.neighbor.Controller;


import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Service.NeighborService;
import com.playblog.blogservice.neighbor.dto.*;
import com.playblog.blogservice.neighbor.mapper.NeighborDtoMapper;
import com.playblog.blogservice.userInfo.UserInfo;
import com.playblog.blogservice.userInfo.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/neighbors")
@RequiredArgsConstructor
public class NeighborController {
    private final NeighborService neighborService;
    private final NeighborDtoMapper neighborDtoMapper;
    private final UserInfoRepository userInfoRepository;


    // 내가 요청한 이웃(내가 추가)
    @GetMapping("/my-following/added")
    public ResponseEntity<List<MyAddedForMeNeighborDto>> getMyAddedNeighbors(
            @AuthenticationPrincipal String userIdStr
    ) {
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        List<Neighbor> neighbors = neighborService.getAddedForMeNeighbors(userId);

        // reverse 관계들 미리 조회해서 Map으로 만듦
        List<UserInfo> toUsers = neighbors.stream()
                .map(Neighbor::getToUserInfo)
                .toList();

        List<Neighbor> reverseList = neighborService.getReverseNeighbors(toUsers, userId);
        Map<Long, Neighbor> reverseMap = reverseList.stream()
                .collect(Collectors.toMap(n -> n.getFromUserInfo().getId(), n -> n));

        List<MyAddedForMeNeighborDto> result = neighbors.stream()
                .map(n -> neighborDtoMapper.toMyAddedDto(n, reverseMap.get(n.getToUserInfo().getId())))
                .toList();

        return ResponseEntity.ok(result);
    }
    // 나에게 이웃 요청한(나를 추가)
    @GetMapping("/my-following/received")
    public ResponseEntity<List<MyAddedToMeNeighborDto>> getMyReceivedNeighbors(
            @AuthenticationPrincipal String userIdStr
    ) {
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        List<Neighbor> neighbors = neighborService.getAddedToMeNeighbors(userId);

        List<Neighbor> reverseNeighbors = neighborService.getAddedForMeNeighbors(userId);
        List<MyAddedToMeNeighborDto> result = neighborDtoMapper.toMyReceivedDto(neighbors, reverseNeighbors);

        return ResponseEntity.ok(result);
    }


    // 내가 보낸 서로이웃
    @GetMapping("/my-following/sent-mutual")
    public ResponseEntity<List<SentMutualNeighborDto>> getSentMutualNeighbors(
            @AuthenticationPrincipal String userIdStr
    ) {
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환
        List<Neighbor> neighbors = neighborService.getSentMutualNeighbors(userId);
        List<SentMutualNeighborDto> result = neighbors.stream()
                .map(neighborDtoMapper::toSentMutualDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    // 내가 받은 서로이웃
    @GetMapping("/my-following/received-mutual")
    public ResponseEntity<List<ReceivedMutualNeighborDto>> getReceivedMutualNeighbors(
            @AuthenticationPrincipal String userIdStr
    ) {
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        List<Neighbor> neighbors = neighborService.getReceivedMutualNeighbors(userId);
        List<ReceivedMutualNeighborDto> result = neighbors.stream()
                .map(neighborDtoMapper::toReceivedMutualDto)
                .toList();
        return ResponseEntity.ok(result);
    }



    // 이웃 요청(다수)
    @PatchMapping("/accept")
    public ResponseEntity<Void> insertNeighbors(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody List<Long> insertUserIds
    ){
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        neighborService.acceptNeighborsStatus(userId,insertUserIds);
        return ResponseEntity.noContent().build();
    }
    // 이웃 요청(한명)
    @PatchMapping("/{insertUserId}/accept")
    public ResponseEntity<Void> insertNeighbor(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable Long insertUserId
    ){
        System.out.println("🧪 @AuthenticationPrincipal userIdStr = " + userIdStr);
        System.out.println("🧪 @PathVariable insertUserId = " + insertUserId);

        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        neighborService.acceptNeighbor(userId,insertUserId);
        return ResponseEntity.noContent().build();
    }


    // 서로이웃 수락(단체)
    @PostMapping("/batch-accept")
    public ResponseEntity<Void> acceptMultipleNeighbors(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody List<Long> Ids
    ){
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        neighborService.accpetMultipleNeighbors(userId,Ids);
        return ResponseEntity.noContent().build();
    }

    // 서로 이웃 거절(단체)
    @PostMapping("/batch-rejected")
    public ResponseEntity<Void> rejectMultipleNeighbors(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody List<Long> Ids
    ){
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        neighborService.rejectMultipleNeighbors(userId,Ids);
        return ResponseEntity.noContent().build();
    }
    // 이웃관계 삭제(이웃에서 아예 삭제)
    @DeleteMapping("/delete")
    public ResponseEntity<Void> rejectAllNeighbors(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody List<Long> deleteUserId
    ){
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        neighborService.rejectAllRelationNeighbor(userId,deleteUserId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/batch-change")
    public ResponseEntity<Void> changeRelationNeighbors(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody List<Long> changeUserId
    ){
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        neighborService.changeRelationNeighbor(userId,changeUserId);
        return ResponseEntity.noContent().build();
    }
    // 내가 보낸 신청 취소
    @PostMapping("/batch-cancel")
    public ResponseEntity<Void> cancelRequestNeighbors(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody List<Long> cancelUserIds
    ){
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환
        neighborService.cancelRequestNeighbors(userId,cancelUserIds);
        return ResponseEntity.noContent().build();
    }
    // 유저 차단
    @PostMapping("/batch-block")
    public ResponseEntity<Void> blockNeighbors(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody List<Long> blockUserIds
            ){
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);  // 👈 여기서 안전하게 변환

        neighborService.blockNeighbors(userId,blockUserIds);
        return ResponseEntity.noContent().build();
    }
    // 차단 유저 조회
    @GetMapping("/blocked")
    public ResponseEntity<List<BlockedForMeNeighborDto>> blockedNeighbors(
            @AuthenticationPrincipal String userIdStr
    ){
        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);
        List<Neighbor> blockedNeighbors = neighborService.getBlockedNeighbors(userId);
        List<BlockedForMeNeighborDto> result = blockedNeighbors.stream()
                .map(neighborDtoMapper::blockedForMeNeighborDto)
                .toList();
        return ResponseEntity.ok(result);
    }
    // 로그인 정보
    @GetMapping("/saved")
    public ResponseEntity<LoginUserDto> savedNeighbors(@AuthenticationPrincipal String userIdStr) {
        System.out.println("[🔍 Principal userIdStr] " + userIdStr);

        if (userIdStr == null) throw new RuntimeException("로그인 필요");
        Long userId = Long.valueOf(userIdStr);

        System.out.println("[✅ Parsed userId] " + userId);
        UserInfo loginUser = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("[🎯 loginUser] " + loginUser.getNickname());

        LoginUserDto userInfo = neighborDtoMapper.toLoginUserDto(loginUser);

        return ResponseEntity.ok(userInfo);
    }
    // 해당 유저정보 넘겨주기
    @GetMapping("/by-nickname/{nickname}")
    public ResponseEntity<insertPopup> getUserByNickname(
            @PathVariable String nickname) {햐
        System.out.println("📌 요청 받은 닉네임: [" + nickname + "]");
        UserInfo user = userInfoRepository.findByNickname(nickname);
        System.out.println(user.toString());

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(neighborDtoMapper.toInsertPopup(user));
    }
}
