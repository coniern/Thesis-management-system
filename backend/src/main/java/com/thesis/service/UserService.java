package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thesis.entity.User;
import com.thesis.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public String generatePasswordHash(String password) {
        return passwordEncoder.encode(password);
    }

    @Cacheable(value = "users", key = "#username", unless = "#result == null")
    public User findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            User user = userMapper.selectOne(wrapper);
            logger.debug("Found user: {}", user != null ? user.getUsername() : "null");
            if (user != null) {
                logger.debug("User password: {}", user.getPassword());
                logger.debug("Password length: {}", user.getPassword() != null ? user.getPassword().length() : 0);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error finding user by username: {}", username, e);
            throw e;
        }
    }

    public User findById(Long id) {
        logger.debug("Finding user by id: {}", id);
        try {
            User user = userMapper.selectById(id);
            logger.debug("Found user: {}", user != null ? user.getUsername() : "null");
            return user;
        } catch (Exception e) {
            logger.error("Error finding user by id: {}", id, e);
            throw e;
        }
    }

    @CacheEvict(value = "users", key = "#user.username")
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 确保角色名称正确格式化（转换为小写）
        if (user.getRole() != null) {
            user.setRole(user.getRole().toLowerCase());
        }
        userMapper.insert(user);
        return user;
    }

    public void update(User user) {
        userMapper.updateById(user);
    }

    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    public List<User> getTeachers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "teacher");
        return userMapper.selectList(wrapper);
    }

    public List<User> getTeachersByDepartmentAndMajor(String department, String major) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "teacher");
        if (department != null && !department.isEmpty()) {
            wrapper.eq(User::getDepartment, department);
        }
        if (major != null && !major.isEmpty()) {
            wrapper.eq(User::getMajor, major);
        }
        return userMapper.selectList(wrapper);
    }

    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 确保角色名称正确格式化（转换为小写）
        if (user.getRole() != null) {
            user.setRole(user.getRole().toLowerCase());
        }
        userMapper.insert(user);
        return user;
    }
}