package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thesis.entity.Notification;
import com.thesis.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    public void createNotification(String title, String content, Long publisherId, String receiveRole) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setPublisherId(publisherId);
        notification.setReceiveRole(receiveRole);
        notification.setPublishTime(LocalDateTime.now());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    public void createGlobalNotification(String title, String content, Long publisherId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setPublisherId(publisherId);
        notification.setReceiveRole("all"); // all表示全局通知
        notification.setPublishTime(LocalDateTime.now());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        // 这里需要根据用户角色获取通知
        // 暂时返回所有通知，后续可以根据实际需求实现角色过滤
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notification::getPublishTime);
        return notificationMapper.selectList(wrapper);
    }

    public void markAsRead(Long notificationId) {
        // 由于数据库表中没有status字段，暂时不实现此功能
        // 后续可以根据实际需求添加status字段
    }

    public void markAllAsRead(Long userId) {
        // 由于数据库表中没有status字段，暂时不实现此功能
        // 后续可以根据实际需求添加status字段
    }

    public void deleteNotification(Long id) {
        notificationMapper.deleteById(id);
    }

    public void updateNotification(Long id, String title, String content) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getId, id)
                .set(Notification::getTitle, title)
                .set(Notification::getContent, content)
                .set(Notification::getUpdatedAt, LocalDateTime.now());
        notificationMapper.update(null, wrapper);
    }
}