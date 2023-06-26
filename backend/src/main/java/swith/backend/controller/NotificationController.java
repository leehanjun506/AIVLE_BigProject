package swith.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swith.backend.domain.Pose;
import swith.backend.domain.User;
import swith.backend.dto.PoseRequestDto;
import swith.backend.dto.PoseRespondDto;
import swith.backend.repository.PoseRepository;
import swith.backend.repository.UserRepository;
import swith.backend.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final PoseRepository poseRepository;
    private final ObjectMapper mapper;

    @GetMapping(value = "/subscribe/{serialNumber}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @PathVariable String serialNumber) {
        User user = userRepository.findBySerialNumber(serialNumber).get();
        Long id = user.getId();

        return notificationService.subscribe(id);
    }

    @PostMapping("/send-data")
    public void sendDataTest(@RequestBody PoseRequestDto poseRequestDto) throws JsonProcessingException {
        String serialNumber = poseRequestDto.getSerialNumber();
        User user = userRepository.findBySerialNumber(serialNumber).get();
        Long id = user.getId();

        PoseRespondDto poseRespondDto = new PoseRespondDto(label_to_string(poseRequestDto.getLabel()),poseRequestDto.getWifi(),poseRequestDto.getCamera());
        String poseR = mapper.writeValueAsString(poseRespondDto);
        notificationService.notify(id, poseR);
    }

    @PostMapping("/send-db")
    public void sendDataDb(@RequestBody PoseRequestDto poseRequestDto){
        String serialNumber = poseRequestDto.getSerialNumber();
        User user = userRepository.findBySerialNumber(serialNumber).get();

        Pose pose = Pose.builder()
                .label(poseRequestDto.getLabel())
                .wifi(poseRequestDto.getWifi())
                .camera(poseRequestDto.getCamera())
                .build();
        pose.label_to_string(poseRequestDto.getLabel());
        pose.setUser(user);
        poseRepository.save(pose);
    }
    public String label_to_string(int label) {
        if (label == 0) {
            return "낙상이 감지되었습니다.";
        } else if (label == 1) {
            return "뒤집힘이 감지되었습니다.";
        } else {
            return "사각 지대에서 움직임이 감지되었습니다.";
        }
    }
}
